package com.training.backend.payload.response;

import com.training.backend.dto.CertificationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificationResponse {
    private String code;
    private List<CertificationDTO> certifications;

}
