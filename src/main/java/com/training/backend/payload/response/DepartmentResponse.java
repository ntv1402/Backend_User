package com.training.backend.payload.response;

import com.training.backend.dto.DepartmentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponse {

    private String code;
    private List<DepartmentDTO> departments;

}
