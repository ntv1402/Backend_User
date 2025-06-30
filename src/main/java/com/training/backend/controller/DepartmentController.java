package com.training.backend.controller;

import com.training.backend.constant.MessageConstant;
import com.training.backend.dto.DepartmentDTO;
import com.training.backend.payload.response.DepartmentResponse;
import com.training.backend.payload.response.ErrorResponse;
import com.training.backend.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/departments")
    public ResponseEntity<?> getAllDepartments() {
        try {
            List<DepartmentDTO> departments = departmentService.getAllDepartments();

            DepartmentResponse  departmentResponse = new DepartmentResponse("200", departments);

            return ResponseEntity.ok(departmentResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("500");
            errorResponse.addMessage(MessageConstant.ER015_CODE, Collections.emptyList());
            return ResponseEntity.badRequest().body(errorResponse);

        }
    }

}
