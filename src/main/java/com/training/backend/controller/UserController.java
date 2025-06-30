package com.training.backend.controller;

import com.training.backend.dto.UserDTO;
import com.training.backend.payload.request.FormRequest;
import com.training.backend.payload.request.UserRequest;
import com.training.backend.payload.response.ListResponse;
import com.training.backend.payload.response.SuccessResponse;
import com.training.backend.payload.response.UserDetailResponse;
import com.training.backend.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.training.backend.constant.MessageConstant.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @PostMapping("/list")
    public ListResponse listUser(
            @RequestParam(value = REQUEST_FULL_NAME, required = false) String fullname,
            @RequestParam(value = REQUEST_DEPARTMENT, required = false) String departmentId,
            @RequestParam(value = REQUEST_ORD_FULLNAME, required = false) String ordFullname,
            @RequestParam(value = REQUEST_ORD_CERTIFICATE, required = false) String ordCertificationName,
            @RequestParam(value = REQUEST_ORD_CERTIFICATION_DATE, required = false) String ordEndDate,
            @RequestParam(value = REQUEST_OFFSET, required = false) Integer offset,
            @RequestParam(value = REQUEST_LIMIT, required = false) Integer limit) {

        // Set default values
        if (limit == null) {
            limit = 5;
        }
        if (offset == null) {
            offset = 0;
        }

        // Build UserRequest
        UserRequest userRequest = new UserRequest();
        userRequest.setFullname(fullname);
        userRequest.setDepartmentId(departmentId);
        userRequest.setOrdFullname(ordFullname);
        userRequest.setOrdCertificationName(ordCertificationName);
        userRequest.setOrdEndDate(ordEndDate);
        userRequest.setOffset(offset);
        userRequest.setLimit(limit);

        // Get data from service
        Long totalRecords = userService.getCountUsers(userRequest);
        List<UserDTO> userDTOList;
        if (totalRecords > 0) {
            userDTOList = userService.getListUsers(userRequest);
        } else {
            userDTOList = new ArrayList<>();
        }

        return new ListResponse(API_SUCCESS, totalRecords, userDTOList);
    }

    @PostMapping
    public SuccessResponse addUser(@RequestBody FormRequest addRequest) {
        Long newUserId = userService.addUser(addRequest);

        SuccessResponse successResponse = new SuccessResponse(API_SUCCESS, newUserId);
        successResponse.addMessage(MSG001_CODE, Collections.emptyList());
        return successResponse;
    }

    @GetMapping("/{userId}")
    public UserDetailResponse getUserById(@PathVariable Long userId) {
        UserDetailResponse response = userService.getUserById(userId);
        return response;
    }

    @DeleteMapping("/{userId}")
    public SuccessResponse deleteUserById(@PathVariable Long userId) {
        Long deletedUserId = userService.deleteUser(userId);

        SuccessResponse response = new SuccessResponse(API_SUCCESS, deletedUserId);
        response.addMessage(MSG003_CODE, Collections.emptyList());
        return response;
    }

    @PutMapping
    public SuccessResponse updateUser(@RequestBody FormRequest updateRequest) {
        Long updateUserId = userService.updateUser(updateRequest);

        SuccessResponse successResponse = new SuccessResponse(API_SUCCESS, updateUserId);
        successResponse.addMessage(MSG002_CODE, Collections.emptyList());
        return successResponse;
    }
}
