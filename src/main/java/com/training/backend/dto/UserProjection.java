package com.training.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface UserProjection {
    Long getUserId();

    String getFullname();

    String getEmail();

    LocalDate getBirthDate();

    String getDepartmentName();

    String getTelephone();

    String getCertificationName();

    LocalDate getEndDate();

    BigDecimal getScore();

}
