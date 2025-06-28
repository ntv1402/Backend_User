package com.training.backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailResponse {
    private Long code;
    private Long userId;
    private String fullName;
    private LocalDate birthDate;
    private Long departmentId;
    private String departmentName;
    private String email;
    private String telephone;
    private String katakana;
    private String username;
    private List<CertificationDetail> certifications;

    @Data
    public static class CertificationDetail {
        private Long certificationId;
        private String certificationName;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal score;
    }
}
