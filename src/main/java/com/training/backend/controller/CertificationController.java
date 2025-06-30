package com.training.backend.controller;

import com.training.backend.constant.MessageConstant;
import com.training.backend.dto.CertificationDTO;
import com.training.backend.payload.response.CertificationResponse;
import com.training.backend.payload.response.ErrorResponse;
import com.training.backend.service.CertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CertificationController {

    @Autowired
    private CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @GetMapping("/certifications")
    public ResponseEntity<?> getAllCertifications() {
        try {
            List<CertificationDTO> certifications = certificationService.getAllCertifications();

            CertificationResponse certificationResponse = new CertificationResponse();

            return   ResponseEntity.ok(certificationResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("500");
            errorResponse.addMessage(MessageConstant.ER015_CODE, Collections.emptyList());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
