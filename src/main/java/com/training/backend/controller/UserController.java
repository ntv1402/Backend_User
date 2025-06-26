package com.training.backend.controller;

import com.training.backend.payload.request.UserRequest;
import com.training.backend.payload.response.ListResponse;
import com.training.backend.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserServiceImpl  userService;

    @PostMapping("/list")
    public ListResponse listUser(
            @RequestParam(value = "fullname", required = false) String fullname,
            @RequestParam(value = "departmentId", required = false) String departmentId,
            @RequestParam(value = "ordFullname", required = false) String ordFullname,
            @RequestParam(value = "ordCertificationName", required = false) String ordCertificationName,
            @RequestParam(value = "ordEndDate", required = false) String ordEndDate,
            @RequestParam(value = "offset", required = false) String offset,
            @RequestParam(value = "limit", required = false) String limit) {


        return ListResponse(listUser());
    }
}
