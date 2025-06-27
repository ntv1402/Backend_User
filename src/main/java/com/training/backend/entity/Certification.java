package com.training.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "certifications")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_id")
    private Long certificationId;

    @Column(name = "certification_name", length = 50, nullable = false)
    private String certificationName;

    @Column(name = "certification_level", nullable = false)
    private Integer certificationLevel;

    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL)
    private List<UserCerti>  userCerti;
}
