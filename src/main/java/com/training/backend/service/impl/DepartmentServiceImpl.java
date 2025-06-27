package com.training.backend.service.impl;

import com.training.backend.dto.DepartmentDTO;
import com.training.backend.entity.Department;
import com.training.backend.mapper.DepartmentMapper;
import com.training.backend.repository.DepartmentRepository;
import com.training.backend.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentMapper mapper;

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        List<Department> list = departmentRepository.findAll();
        return mapper.toDtoList(list);
    }
}
