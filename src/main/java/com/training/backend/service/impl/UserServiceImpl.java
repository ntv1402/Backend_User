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

    @Override
    public List<UserDTO> listUsers(UserRequest userRequest) {
        List<UserProjection> projections = userRepository.searchUsers(
                userRequest.getFullname(),
                userRequest.getDepartmentId(),
                userRequest.getOrdFullname(),
                userRequest.getOrdCertificationName(),
                userRequest.getOrdEndDate(),
                userRequest.getOffset(),
                userRequest.getLimit()
        );

        List<UserDTO> users = projections.stream().map(proj -> {
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
        }).collect(Collectors.toList());
        return users;
    }

    @Override
    public Long countUsers(UserRequest userRequest) {
        List<UserProjection> projections = userRepository.searchUsers(
                userRequest.getFullname(),
                userRequest.getDepartmentId(),
                userRequest.getOrdFullname(),
                userRequest.getOrdCertificationName(),
                userRequest.getOrdEndDate(),
                userRequest.getOffset(),
                userRequest.getLimit()
        );

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
            LocalDate birthDate = null;
            if (addRequest.getBirthDate() != null && !addRequest.getBirthDate().isEmpty()) {
                // FE gửi ngày dạng "yyyy/MM/dd", ví dụ "2025/05/01"
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MessageConstant.DATE_FORMAT);
                birthDate = LocalDate.parse(addRequest.getBirthDate(), formatter);
            }

            User user = new User();

            user.setFullname(addRequest.getFullname());
            user.setBirthdate(birthDate);
            user.setEmail(addRequest.getEmail());
            user.setTelephone(addRequest.getTelephone());
            user.setKatakana(addRequest.getKatakana());
            user.setUsername(addRequest.getUsername());
            user.setPassword(passwordEncoder.encode(addRequest.getPassword()));
            user.setDepartmentId(addRequest.getDepartmentId());

            User savedUser = userRepository.save(user);

            if (addRequest.getCertifications() != null) {
                List<CertificationRequest> certificationRequests = addRequest.getCertifications();
                for (CertificationRequest certRequest : certificationRequests) {
                    UserCerti userCerti = new UserCerti();
                    userCerti.setUserId(savedUser);
                    Certification certification = new Certification();
                    certification.setCertificationId(certRequest.getCertificationId());
                    userCerti.setCertification(certification);
                    if (certRequest.getCertificationStartDate() != null && !certRequest.getCertificationStartDate().isEmpty()) {
                        userCerti.setStartDate(LocalDate.parse(certRequest.getCertificationStartDate(), DateTimeFormatter.ofPattern(MessageConstant.DATE_FORMAT)));
                    }
                    if (certRequest.getCertificationEndDate() != null && !certRequest.getCertificationEndDate().isEmpty()) {
                        userCerti.setEndDate(LocalDate.parse(certRequest.getCertificationEndDate(), DateTimeFormatter.ofPattern(MessageConstant.DATE_FORMAT)));
                    }

                    userCerti.setScore(certRequest.getUserCertificationScore());

                    userCertiRepository.save(userCerti);
                }
            }
            return savedUser.getUserId();
        } catch (Exception e) {
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

        User user = userRepository.findById(userId).get();

        UserDetailResponse userDetailResponse = new UserDetailResponse();
        userDetailResponse.setCode(200L);
        userDetailResponse.setUserId(userId);
        userDetailResponse.setFullName(user.getFullname());
        userDetailResponse.setKatakana(user.getKatakana());
        userDetailResponse.setUsername(user.getUsername());
        userDetailResponse.setEmail(user.getEmail());
        userDetailResponse.setBirthDate(user.getBirthdate());
        userDetailResponse.setTelephone(user.getTelephone());
        userDetailResponse.setDepartmentId(user.getDepartmentId());

        Department department = departmentRepository.findById(user.getDepartmentId()).get();
        userDetailResponse.setDepartmentName(department.getDepartmentName());

        List<UserCerti> userCertis = userCertiRepository.findByUserId(userId);
        List<UserDetailResponse.CertificationDetail> userDetailResponseList = new ArrayList<>();

        for (UserCerti userCerti : userCertis) {
            UserDetailResponse.CertificationDetail certificationDetail = new UserDetailResponse.CertificationDetail();
            certificationDetail.setCertificationId(userCerti.getCertification().getCertificationId());
            certificationDetail.setCertificationName(userCerti.getCertification().getCertificationName());
            certificationDetail.setStartDate(userCerti.getStartDate());
            certificationDetail.setEndDate(userCerti.getEndDate());
            certificationDetail.setScore(userCerti.getScore());
            userDetailResponseList.add(certificationDetail);
        }
        userDetailResponse.setCertifications(userDetailResponseList);
        return userDetailResponse;
    }

    @Override
    @Transactional
    public Long updateUser(FormRequest updateRequest) {
        LocalDate birthDate = null;
        if (updateRequest.getBirthDate() != null && !updateRequest.getBirthDate().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MessageConstant.DATE_FORMAT);
            birthDate = LocalDate.parse(updateRequest.getBirthDate(), formatter);
        }

        User user = userRepository.findById(updateRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setFullname(updateRequest.getFullname());
        user.setBirthdate(birthDate);
        user.setKatakana(updateRequest.getKatakana());
        user.setEmail(updateRequest.getEmail());
        user.setTelephone(updateRequest.getTelephone());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        user.setDepartmentId(updateRequest.getDepartmentId());

        final User savedUser = userRepository.save(user);

        if (updateRequest.getCertifications() != null && !updateRequest.getCertifications().isEmpty()) {

            userCertiRepository.deleteByUserId(savedUser.getUserId());

            List<UserCerti> userCertis = updateRequest.getCertifications().stream()
                    .map(certRequest -> {
                        Certification certification = certificationRepository.findById(certRequest.getCertificationId()).orElseThrow(() -> new NotFoundException("Certification not found"));

                        UserCerti userCerti = new UserCerti();
                        userCerti.setUserId(savedUser);
                        userCerti.setCertification(certification);

                        if (certRequest.getCertificationStartDate() != null && !certRequest.getCertificationStartDate().isEmpty()) {
                            userCerti.setStartDate(LocalDate.parse(certRequest.getCertificationStartDate(), DateTimeFormatter.ofPattern(MessageConstant.DATE_FORMAT)));
                        }

                        if (certRequest.getCertificationEndDate() != null && !certRequest.getCertificationEndDate().isEmpty()) {
                            userCerti.setEndDate(LocalDate.parse(certRequest.getCertificationEndDate(), DateTimeFormatter.ofPattern(MessageConstant.DATE_FORMAT)));
                        }

                        userCerti.setScore(certRequest.getUserCertificationScore());
                        return userCerti;
                    })
                    .collect(Collectors.toList());

            userCertiRepository.saveAll(userCertis);
        }
        return savedUser.getUserId();
    }
}
