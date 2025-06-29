package com.training.backend.service.impl;

import com.training.backend.config.MessageConstant;
import com.training.backend.dto.UserDTO;
import com.training.backend.dto.UserProjection;
import com.training.backend.entity.Certification;
import com.training.backend.entity.Department;
import com.training.backend.entity.User;
import com.training.backend.entity.UserCerti;
import com.training.backend.exception.NotFoundException;
import com.training.backend.payload.request.CertificationRequest;
import com.training.backend.payload.request.FormRequest;
import com.training.backend.payload.request.UserRequest;
import com.training.backend.payload.response.ErrorResponse;
import com.training.backend.payload.response.UserDetailResponse;
import com.training.backend.repository.CertificationRepository;
import com.training.backend.repository.DepartmentRepository;
import com.training.backend.repository.UserCertiRepository;
import com.training.backend.repository.UserRepository;
import com.training.backend.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private UserCertiRepository userCertiRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Helper method để set dữ liệu cho UserCerti
     */
    private UserCerti setUserCertiData(User user, CertificationRequest certRequest) {
        UserCerti userCerti = new UserCerti();
        userCerti.setUser(user);
        
        // Tìm certification từ database
        Certification certification = certificationRepository.findById(certRequest.getCertificationId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy Certification"));
        userCerti.setCertification(certification);
        
        // Set start date
        if (certRequest.getCertificationStartDate() != null && !certRequest.getCertificationStartDate().isEmpty()) {
            userCerti.setStartDate(parseDate(certRequest.getCertificationStartDate()));
        }
        
        // Set end date
        if (certRequest.getCertificationEndDate() != null && !certRequest.getCertificationEndDate().isEmpty()) {
            userCerti.setEndDate(parseDate(certRequest.getCertificationEndDate()));
        }
        
        // Set score
        userCerti.setScore(certRequest.getUserCertificationScore());
        
        return userCerti;
    }

    /**
     * Helper method để parse ngày tháng từ string sang LocalDate
     */
    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MessageConstant.DATE_FORMAT);
        return LocalDate.parse(dateString, formatter);
    }

    /**
     * Helper method để set dữ liệu cho User từ FormRequest
     */
    private void setUserData(User user, FormRequest request) {
        user.setFullname(request.getFullname());
        user.setBirthdate(parseDate(request.getBirthDate()));
        user.setEmail(request.getEmail());
        user.setTelephone(request.getTelephone());
        user.setKatakana(request.getKatakana());
        user.setUsername(request.getUsername());
        user.setDepartmentId(request.getDepartmentId());
    }

    /**
     * Helper method để set password cho User (nếu có)
     */
    private void setUserPassword(User user, String password) {
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }
    }

    /**
     * Helper method để xử lý certifications từ request
     */
    private void processCertifications(User user, List<CertificationRequest> certifications) {
        if (certifications != null && !certifications.isEmpty()) {
            for (CertificationRequest certRequest : certifications) {
                UserCerti userCerti = setUserCertiData(user, certRequest);
                userCertiRepository.save(userCerti);
            }
        }
    }

    /**
     * Helper method để xử lý certifications khi update (xóa cũ, thêm mới)
     */
    private void processCertificationsForUpdate(User user, List<CertificationRequest> certifications) {
        if (certifications != null && !certifications.isEmpty()) {
            // Xóa certifications cũ
            userCertiRepository.deleteByUserId(user.getUserId());
            
            // Thêm certifications mới
            List<UserCerti> userCertis = certifications.stream()
                    .map(certRequest -> setUserCertiData(user, certRequest))
                    .collect(Collectors.toList());
            
            userCertiRepository.saveAll(userCertis);
        }
    }

    /**
     * Helper method để map từ UserProjection sang UserDTO
     */
    private UserDTO mapProjectionToDTO(UserProjection proj) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(proj.getUserId());
        userDTO.setFullname(proj.getFullname());
        userDTO.setDepartmentName(proj.getDepartmentName());
        userDTO.setBirthdate(proj.getBirthDate());
        userDTO.setEmail(proj.getEmail());
        userDTO.setTelephone(proj.getTelephone());
        userDTO.setCertificationName(proj.getCertificationName());
        userDTO.setEndDate(proj.getEndDate());
        userDTO.setScore(proj.getScore());
        return userDTO;
    }

    /**
     * Helper method để map từ UserCerti sang CertificationDetail
     */
    private UserDetailResponse.CertificationDetail mapUserCertiToDetail(UserCerti userCerti) {
        UserDetailResponse.CertificationDetail certificationDetail = new UserDetailResponse.CertificationDetail();
        certificationDetail.setCertificationId(userCerti.getCertification().getCertificationId());
        certificationDetail.setCertificationName(userCerti.getCertification().getCertificationName());
        certificationDetail.setStartDate(userCerti.getStartDate());
        certificationDetail.setEndDate(userCerti.getEndDate());
        certificationDetail.setScore(userCerti.getScore());
        return certificationDetail;
    }

    /**
     * Helper method để set dữ liệu cho UserDetailResponse từ User và Department
     */
    private void setUserDetailResponseData(UserDetailResponse response, User user, Department department) {
        response.setCode(200L);
        response.setUserId(user.getUserId());
        response.setFullName(user.getFullname());
        response.setKatakana(user.getKatakana());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setBirthDate(user.getBirthdate());
        response.setTelephone(user.getTelephone());
        response.setDepartmentId(user.getDepartmentId());
        response.setDepartmentName(department.getDepartmentName());
    }

    /**
     * Helper method để lấy projections từ repository
     */
    private List<UserProjection> getProjections(UserRequest userRequest) {
        return userRepository.searchUsers(
                userRequest.getFullname(),
                userRequest.getDepartmentId(),
                userRequest.getOrdFullname(),
                userRequest.getOrdCertificationName(),
                userRequest.getOrdEndDate(),
                userRequest.getOffset(),
                userRequest.getLimit()
        );
    }

    @Override
    public List<UserDTO> listUsers(UserRequest userRequest) {
        List<UserProjection> projections = getProjections(userRequest);

        List<UserDTO> users = projections.stream()
                .map(this::mapProjectionToDTO)
                .collect(Collectors.toList());
        return users;
    }

    @Override
    public Long countUsers(UserRequest userRequest) {
        List<UserProjection> projections = getProjections(userRequest);

        long totalRecords = userRepository.countUsers(
                userRequest.getFullname(),
                userRequest.getDepartmentId()
        );
        return totalRecords;
    }

    @Transactional
    @Override
    public Long addUser(FormRequest addRequest) {
        try {
            LocalDate birthDate = parseDate(addRequest.getBirthDate());

            User user = new User();

            setUserData(user, addRequest);
            setUserPassword(user, addRequest.getPassword());

            User savedUser = userRepository.save(user);

            if (addRequest.getCertifications() != null) {
                List<CertificationRequest> certificationRequests = addRequest.getCertifications();
                processCertifications(savedUser, certificationRequests);
            }
            return savedUser.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional
    public Long deleteUser(Long userId) {
        try {
            userCertiRepository.deleteByUserId(userId);
            userRepository.deleteById(userId);
            return userId;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public UserDetailResponse getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy User"));

        UserDetailResponse userDetailResponse = new UserDetailResponse();
        setUserDetailResponseData(userDetailResponse, user, departmentRepository.findById(user.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy Department")));

        List<UserCerti> userCertis = userCertiRepository.findByUserId(userId);
        List<UserDetailResponse.CertificationDetail> userDetailResponseList = userCertis.stream()
                .map(this::mapUserCertiToDetail)
                .collect(Collectors.toList());
        
        userDetailResponse.setCertifications(userDetailResponseList);
        return userDetailResponse;
    }

    @Override
    @Transactional
    public Long updateUser(FormRequest updateRequest) {
        User user = userRepository.findById(updateRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy User"));
        
        setUserData(user, updateRequest);
        setUserPassword(user, updateRequest.getPassword());

        final User savedUser = userRepository.save(user);

        if (updateRequest.getCertifications() != null && !updateRequest.getCertifications().isEmpty()) {
            processCertificationsForUpdate(savedUser, updateRequest.getCertifications());
        }
        return savedUser.getUserId();
    }
}
