package com.training.backend.controller;

import com.training.backend.dto.UserDTO;
import com.training.backend.payload.request.FormRequest;
import com.training.backend.payload.request.UserRequest;
import com.training.backend.payload.response.ListResponse;
import com.training.backend.payload.response.SuccessResponse;
import com.training.backend.payload.response.UserDetailResponse;
import com.training.backend.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.training.backend.config.MessageConstant.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @PostMapping("/list")
    public ListResponse listUser(
            @RequestParam(value = "fullname", required = false) String fullname,
            @RequestParam(value = "departmentId", required = false) String departmentId,
            @RequestParam(value = "ordFullname", required = false) String ordFullname,
            @RequestParam(value = "ordCertificationName", required = false) String ordCertificationName,
            @RequestParam(value = "ordEndDate", required = false) String ordEndDate,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit) {

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
        Long totalRecords = userService.countUsers(userRequest);
        List<UserDTO> userDTOList;
        if (totalRecords > 0) {
            userDTOList = userService.listUsers(userRequest);
        } else {
            userDTOList = new ArrayList<>();
        }
        
        return new ListResponse(API_SUCCESS, totalRecords, userDTOList);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse> addUser(@RequestBody FormRequest addRequest) {
        // Service sẽ tự động validate và throw exception nếu có lỗi
        Long newUserId = userService.addUser(addRequest);

        SuccessResponse successResponse = new SuccessResponse(API_SUCCESS, newUserId);
        successResponse.addMessage(MSG001_CODE, Collections.emptyList());
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable Long userId) {
        // Service sẽ tự động throw NotFoundException nếu không tìm thấy
        UserDetailResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<SuccessResponse> deleteUserById(@PathVariable Long userId) {
        // Service sẽ tự động throw NotFoundException nếu không tìm thấy
        Long deletedUserId = userService.deleteUser(userId);

        SuccessResponse response = new SuccessResponse(API_SUCCESS, deletedUserId);
        response.addMessage(MSG003_CODE, Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<SuccessResponse> updateUser(@RequestBody FormRequest updateRequest) {
        // Service sẽ tự động validate và throw exception nếu có lỗi
        Long updateUserId = userService.updateUser(updateRequest);

        SuccessResponse successResponse = new SuccessResponse(API_SUCCESS, updateUserId);
        successResponse.addMessage(MSG002_CODE, Collections.emptyList());
        return ResponseEntity.ok(successResponse);
    }
}
