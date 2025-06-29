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

    public Long getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(Long certificationId) {
        this.certificationId = certificationId;
    }

    public String getCertificationName() {
        return certificationName;
    }

    public void setCertificationName(String certificationName) {
        this.certificationName = certificationName;
    }

    public Integer getCertificationLevel() {
        return certificationLevel;
    }

    public void setCertificationLevel(Integer certificationLevel) {
        this.certificationLevel = certificationLevel;
    }
}
