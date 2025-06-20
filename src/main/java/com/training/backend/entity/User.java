package com.training.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true)
    private Long Id;

    @Column(name = "fullness")
    private String fullname;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
}
