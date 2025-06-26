package com.training.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true)
    private Long userId;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "katakana")
    private String katakana;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
}
