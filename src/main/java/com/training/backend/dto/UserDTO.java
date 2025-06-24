package com.training.backend.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserDTO {
    private Long id;
    private String fullname;
    private LocalDate birthdate;
    private String departmentName;
    private String email;
    private String telephone;
    private String certificationName;
    private LocalDate endDate;
    private BigDecimal score;
}
