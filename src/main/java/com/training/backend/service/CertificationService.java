package com.training.backend.service;

import com.training.backend.dto.CertificationDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CertificationService {
    List<CertificationDTO> getAllCertifications();
}
