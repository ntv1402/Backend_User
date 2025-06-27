package com.training.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "departments")
public class Department {

    @Id
    @Column(name = "department_id", unique = true)
    private Long departmentId;

    @Column(name = "department_name")
    private String departmentName;
}
