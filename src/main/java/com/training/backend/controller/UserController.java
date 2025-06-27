package com.training.backend.controller;

import com.training.backend.dto.UserDTO;
import com.training.backend.payload.request.UserRequest;
import com.training.backend.payload.response.ListResponse;
import com.training.backend.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.training.backend.config.MessageConstant.API_SUCCESS;

@RestController
@RequestMapping("/employee")
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

        UserRequest userRequest = new UserRequest();
        userRequest.setFullname(fullname);
        userRequest.setDepartmentId(departmentId);
        userRequest.setOrdFullname(ordFullname);
        userRequest.setOrdCertificationName(ordCertificationName);
        userRequest.setOrdEndDate(ordEndDate);
        userRequest.setOffset(offset);
        userRequest.setLimit(limit);

        Long totalRecords = userService.countUsers(userRequest);
        List<UserDTO> userDTOList;
        if (totalRecords > 0) {
            userDTOList = userService.listUsers(userRequest);
        } else {
            userDTOList = new ArrayList<>();
        }

        return new ListResponse(API_SUCCESS, totalRecords, userDTOList);
    }
}
