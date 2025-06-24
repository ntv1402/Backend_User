package com.training.backend.payload.request;

import lombok.Data;


@Data
public class UserRequest {

    private String fullname;

    private String departmentId;

    private String ordFullname;

    private String ordCertificationName;

    private String ordEndDate;

    private String offset;

    private String limit;

}
