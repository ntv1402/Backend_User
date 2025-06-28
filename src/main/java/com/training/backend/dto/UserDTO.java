package com.training.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId;
    private String fullname;
    private LocalDate birthdate;
    private String departmentName;
    private String email;
    private String telephone;
    private String certificationName;
    private LocalDate endDate;
    private BigDecimal score;
}
