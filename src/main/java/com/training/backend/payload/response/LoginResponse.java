package com.training.backend.payload.response;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private Map<String,String> errors = new HashMap<>();

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }

    public LoginResponse(Map<String, String> errors) {
        this.errors = errors;
    }
}
