package com.training.backend.service.impl;

import com.training.backend.dto.CertificationDTO;
import com.training.backend.entity.Certification;
import com.training.backend.mapper.CertificationMapper;
import com.training.backend.repository.CertificationRepository;
import com.training.backend.service.CertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CertificationServiceImpl implements CertificationService {
    @Autowired
    CertificationRepository certificationRepository;

    @Autowired
    CertificationMapper  certificationMapper;

    @Override
    public List<CertificationDTO> getAllCertifications() {
        List<Certification> certificationList = certificationRepository.findAll();
        return  certificationMapper.toDtoList(certificationList);
    }
}
