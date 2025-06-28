package com.training.backend.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificationRequest {
    private Long certificationId;
    private String certificationStartDate;
    private String certificationEndDate;
    private BigDecimal userCertificationScore;
}
