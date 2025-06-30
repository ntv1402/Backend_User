package com.training.backend.service.impl;

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
import com.training.backend.payload.response.UserDetailResponse;
import com.training.backend.repository.CertificationRepository;
import com.training.backend.repository.DepartmentRepository;
import com.training.backend.repository.UserCertiRepository;
import com.training.backend.repository.UserRepository;
import com.training.backend.service.UserService;
import com.training.backend.utils.DateUtils;
import com.training.backend.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.training.backend.constant.MessageConstant.*;

/**
 * Implementation của UserService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final DepartmentRepository departmentRepository;

    private final CertificationRepository certificationRepository;

    private final UserCertiRepository userCertiRepository;

    private final PasswordEncoder passwordEncoder;

    private final ValidationUtils validationUtils;

    @Override
    public List<UserDTO> getListUsers(UserRequest userRequest) {
        // Validate order fields
        validateOrderFields(userRequest);

        // Validate pagination fields
        validatePaginationFields(userRequest);

        List<UserProjection> projections = getProjections(userRequest);

        List<UserDTO> users = projections.stream()
                .map(this::mapProjectionToDTO)
                .collect(Collectors.toList());
        return users;
    }

    @Override
    public Long getCountUsers(UserRequest userRequest) {
        long totalRecords = userRepository.countUsers(
                userRequest.getFullname(),
                userRequest.getDepartmentId()
        );
        return totalRecords;
    }

    @Transactional
    @Override
    public Long addUser(FormRequest addRequest) {
        // Validate input data
        validateUserData(addRequest, true);

        User user = new User();
        setUserData(user, addRequest);
        setUserPassword(user, addRequest.getPassword());

        User savedUser = userRepository.save(user);

        // Xử lý certifications (thêm mới, không xóa cũ)
        processUserCertifications(savedUser, addRequest.getCertifications(), false);

        return savedUser.getUserId();
    }

    @Override
    @Transactional
    public Long deleteUser(Long userId) {
        // Kiểm tra user có tồn tại không
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(ER004_CODE, FIELD_ACCOUNT);
        }

        userCertiRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
        return userId;
    }

    @Override
    @Transactional
    public UserDetailResponse getUserById(Long userId) {

        // Tìm user id
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ER004_CODE, FIELD_ACCOUNT));

        // Tạo object
        UserDetailResponse userDetailResponse = new UserDetailResponse();

        // Tìm department
        setUserDetailResponseData(userDetailResponse, user, departmentRepository.findById(user.getDepartmentId())
                .orElseThrow(() -> new NotFoundException(ER004_CODE, FIELD_GROUP)));

        // Certi
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
        // Validate input data
        validateUserData(updateRequest, false);

        User user = userRepository.findById(updateRequest.getUserId())
                .orElseThrow(() -> new NotFoundException(ER004_CODE, FIELD_ACCOUNT));

        setUserData(user, updateRequest);
        setUserPassword(user, updateRequest.getPassword());

        final User savedUser = userRepository.save(user);

        // Xử lý certifications (xóa cũ + thêm mới)
        processUserCertifications(savedUser, updateRequest.getCertifications(), true);

        return savedUser.getUserId();
    }

    /**
     * Helper method để set dữ liệu cho UserCerti
     */
    private UserCerti setUserCertiData(User user, CertificationRequest certRequest) {
        // Tìm certification từ database
        Certification certification = certificationRepository.findById(certRequest.getCertificationId())
                .orElseThrow(() -> new NotFoundException(ER004_CODE, FIELD_CERTIFICATION));
        
        // Sử dụng Builder pattern để tạo UserCerti
        return UserCerti.builder()
                .user(user)
                .certification(certification)
                .startDate(DateUtils.parseDate(certRequest.getCertificationStartDate()))
                .endDate(DateUtils.parseDate(certRequest.getCertificationEndDate()))
                .score(certRequest.getUserCertificationScore())
                .build();
    }

    /**
     * Helper method để validate dữ liệu user
     * 
     * @param request FormRequest chứa dữ liệu user
     * @param isNewUser True nếu là thêm mới, false nếu là cập nhật
     */
    private void validateUserData(FormRequest request, boolean isNewUser) {
        // Validate username
        validationUtils.validateUsername(request.getUsername(), isNewUser);
        
        // Validate fullname
        validationUtils.validateFullname(request.getFullname());
        
        // Validate katakana
        validationUtils.validateKatakana(request.getKatakana());
        
        // Validate birthdate
        validationUtils.validateBirthdate(request.getBirthDate());
        
        // Validate email
        validationUtils.validateEmail(request.getEmail(), isNewUser, request.getUserId(), isNewUser);
        
        // Validate telephone
        validationUtils.validateTelephone(request.getTelephone());
        
        // Validate password
        validationUtils.validatePassword(request.getPassword(), isNewUser);
        
        // Validate department
        validationUtils.validateDepartmentId(request.getDepartmentId(), true);
        
        // Validate certifications
        validationUtils.validateCertifications(request.getCertifications(), true);
    }

    /**
     * Helper method để set dữ liệu cho User từ FormRequest
     */
    private void setUserData(User user, FormRequest request) {
        try {
            user.setFullname(request.getFullname());
            user.setBirthdate(DateUtils.parseDate(request.getBirthDate()));
            user.setEmail(request.getEmail());
            user.setTelephone(request.getTelephone());
            user.setKatakana(request.getKatakana());
            user.setUsername(request.getUsername());
            user.setDepartmentId(request.getDepartmentId());
        } catch (Exception e) {
            log.error("Error setting user data: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi xử lý dữ liệu user", e);
        }
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

    /**
     * Helper method để xử lý certifications cho user
     * 
     * @param user User cần xử lý certifications
     * @param certifications Danh sách certification requests
     * @param deleteExisting True nếu cần xóa certifications cũ (cho update), false nếu chỉ thêm mới (cho add)
     */
    private void processUserCertifications(User user, List<CertificationRequest> certifications, boolean deleteExisting) {
        if (certifications != null && !certifications.isEmpty()) {
            // Xóa certifications cũ nếu cần (cho update)
            if (deleteExisting) {
                userCertiRepository.deleteByUserId(user.getUserId());
            }
            
            // Thêm certifications mới
            List<UserCerti> userCertis = certifications.stream()
                    .map(certRequest -> setUserCertiData(user, certRequest))
                    .collect(Collectors.toList());
            
            userCertiRepository.saveAll(userCertis);
        }
    }

    /**
     * Helper method để validate tất cả order fields
     * 
     * @param userRequest UserRequest chứa các order fields
     */
    private void validateOrderFields(UserRequest userRequest) {
        validationUtils.validateOrder(userRequest.getOrdFullname(), "orderByFullname");
        validationUtils.validateOrder(userRequest.getOrdCertificationName(), "orderByCodeLevel");
        validationUtils.validateOrder(userRequest.getOrdEndDate(), "orderByEndDate");
    }

    /**
     * Helper method để validate pagination fields
     * 
     * @param userRequest UserRequest chứa limit và offset
     */
    private void validatePaginationFields(UserRequest userRequest) {
        if (userRequest.getLimit() != null) {
            validationUtils.validatePositiveInteger(userRequest.getLimit().toString(), "limit");
        }
        
        if (userRequest.getOffset() != null) {
            validationUtils.validatePositiveInteger(userRequest.getOffset().toString(), "offset");
        }
    }


}
