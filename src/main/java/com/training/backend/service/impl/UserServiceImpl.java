package com.training.backend.service.impl;

import com.training.backend.dto.UserDTO;
import com.training.backend.dto.UserProjection;
import com.training.backend.entity.UserCerti;
import com.training.backend.payload.request.UserRequest;
import com.training.backend.repository.CertificationRepository;
import com.training.backend.repository.DepartmentRepository;
import com.training.backend.repository.UserCertiRepository;
import com.training.backend.repository.UserRepository;
import com.training.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CertificationRepository  certificationRepository;

    @Autowired
    private UserCertiRepository userCertiRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> listUsers (UserRequest userRequest ) {
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



}
