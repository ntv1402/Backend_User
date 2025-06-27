package com.training.backend.payload.response;

import com.training.backend.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListResponse {

    private String code;

    private Long totalRecords;

    private List<UserDTO> userList;

}
