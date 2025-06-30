package com.training.backend.service;

import com.training.backend.dto.UserDTO;
import com.training.backend.entity.User;
import com.training.backend.payload.request.FormRequest;
import com.training.backend.payload.request.UserRequest;
import com.training.backend.payload.response.UserDetailResponse;
import org.apache.coyote.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;


public interface UserService {

    List<UserDTO> getListUsers(UserRequest userRequest);

    Long addUser(FormRequest addRequest);

    UserDetailResponse getUserById (Long Id);

    Long deleteUser(Long Id);

    Long updateUser(FormRequest updateRequest);

    Long getCountUsers(UserRequest userRequest);
}
