package com.training.backend.controller;

import com.training.backend.dto.UserDTO;
import com.training.backend.payload.request.FormRequest;
import com.training.backend.payload.request.UserRequest;
import com.training.backend.payload.response.ErrorResponse;
import com.training.backend.payload.response.ListResponse;
import com.training.backend.payload.response.SuccessResponse;
import com.training.backend.payload.response.UserDetailResponse;
import com.training.backend.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    UserServiceImpl  userService;

    @PostMapping("/list")
    public ListResponse listUser(
            @RequestParam(value = "fullname", required = false) String fullname,
            @RequestParam(value = "departmentId", required = false) String departmentId,
            @RequestParam(value = "ordFullname", required = false) String ordFullname,
            @RequestParam(value = "ordCertificationName", required = false) String ordCertificationName,
            @RequestParam(value = "ordEndDate", required = false) String ordEndDate,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit) {

        if (limit == null) {
            limit = 5;
        }
        if (offset == null) {
            offset = 0;
        }
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

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody FormRequest addRequest) {
        // Validate ở đây

        Long newUserId = userService.addUser(addRequest);

        if (newUserId == null) {
            // Trả về lỗi cho Frontend
            ErrorResponse errorResponse = new ErrorResponse(API_ERROR);
            errorResponse.addMessage(ER015_CODE, Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        SuccessResponse successResponse = new SuccessResponse(API_SUCCESS, newUserId);
        successResponse.addMessage(MSG001_CODE, Collections.emptyList());
        return ResponseEntity.ok().body(successResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            UserDetailResponse response = userService.getUserById(userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse("500");
            errorResponse.addMessage(ER015_CODE, Collections.emptyList());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long userId) {
        try {

            Long deletedUserId = userService.deleteUser(userId);
            if (deletedUserId != null) {
                // Tạo response thành công với mã MSG003
                SuccessResponse response = new SuccessResponse(API_SUCCESS, deletedUserId);
                response.addMessage(MSG003_CODE, Collections.emptyList());
                return ResponseEntity.ok(response);
            } else {
                // Tạo response lỗi với mã ER013 (không tìm thấy)
                ErrorResponse errorResponse = new ErrorResponse(API_ERROR);
                errorResponse.addMessage(ER013_CODE, Collections.singletonList(FIELD_EMPLOYEE_ID));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        } catch (Exception e) {
            // Xử lý lỗi hệ thống
            ErrorResponse errorResponse = new ErrorResponse(API_ERROR);
            errorResponse.addMessage(ER015_CODE, Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateEmployee(@RequestBody FormRequest updateRequest) {
        
        // Nếu tất cả validation đều pass, tiến hành cập nhật nhân viên
        Long updateUserId = userService.updateUser(updateRequest);

        // Tạo response thành công với mã MSG002
        SuccessResponse successResponse = new SuccessResponse(API_SUCCESS, updateUserId);
        successResponse.addMessage(MSG002_CODE, Collections.emptyList());
        return ResponseEntity.ok(successResponse);
    }
}
