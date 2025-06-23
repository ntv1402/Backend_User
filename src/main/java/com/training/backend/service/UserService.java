package com.training.backend.service;

import com.training.backend.entity.User;
import org.apache.coyote.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    ResponseEntity<User> findById(int id);
}
