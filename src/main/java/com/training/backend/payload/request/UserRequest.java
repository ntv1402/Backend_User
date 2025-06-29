package com.training.backend.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String fullname;

    private String departmentId;

    private String ordFullname;

    private String ordCertificationName;

    private String ordEndDate;

    private Integer offset;

    private Integer limit;

}
