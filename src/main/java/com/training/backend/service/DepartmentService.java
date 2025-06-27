package com.training.backend.service;

import com.training.backend.dto.DepartmentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DepartmentService {

    List<DepartmentDTO> getAllDepartments();
}
