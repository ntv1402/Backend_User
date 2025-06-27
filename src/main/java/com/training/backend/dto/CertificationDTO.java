package com.training.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificationDTO {
    private Long certificationId;
    private String certificationName;
    private Integer certificationLevel;
}
