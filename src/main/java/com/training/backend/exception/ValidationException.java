/*
 * Copyright(C) 2025 Luvina JSC
 * ValidationException.java, 17/04/2025 7_nguyenthanhvinh2
 */
package com.training.backend.exception;

import lombok.Getter;

import java.util.List;

/**
 * Exception được throw khi có lỗi validation
 */
@Getter
public class ValidationException extends RuntimeException {
    
    private final String errorCode;
    private final List<String> params;
    
    public ValidationException(String errorCode, String param) {
        super("Validation error: " + errorCode + " - " + param);
        this.errorCode = errorCode;
        this.params = List.of(param);
    }
    
    public ValidationException(String errorCode, List<String> params) {
        super("Validation error: " + errorCode + " - " + String.join(", ", params));
        this.errorCode = errorCode;
        this.params = params;
    }
    
    public ValidationException(String errorCode, String param1, String param2) {
        super("Validation error: " + errorCode + " - " + param1 + ", " + param2);
        this.errorCode = errorCode;
        this.params = List.of(param1, param2);
    }
    
    public ValidationException(String errorCode, String param1, String param2, String param3) {
        super("Validation error: " + errorCode + " - " + param1 + ", " + param2 + ", " + param3);
        this.errorCode = errorCode;
        this.params = List.of(param1, param2, param3);
    }
} 