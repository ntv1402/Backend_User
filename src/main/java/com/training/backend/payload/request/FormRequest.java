package com.training.backend.payload.request;

import com.training.backend.entity.Certification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormRequest {
    private Long userId;
    private String fullname;
    private String birthDate;
    private String email;
    private String telephone;
    private String katakana;
    private String username;
    private String password;
    private Long departmentId;
    private List<CertificationRequest> certifications;
}
