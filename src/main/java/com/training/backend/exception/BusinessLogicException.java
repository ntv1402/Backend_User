/*
 * Copyright(C) 2025 Luvina JSC
 * BusinessLogicException.java, 17/04/2025 7_nguyenthanhvinh2
 */
package com.training.backend.exception;

import lombok.Getter;

import java.util.List;

/**
 * Exception được throw khi có lỗi business logic
 */
@Getter
public class BusinessLogicException extends RuntimeException {
    
    private final String errorCode;
    private final List<String> params;
    
    public BusinessLogicException(String errorCode, String param) {
        super("Business logic error: " + errorCode + " - " + param);
        this.errorCode = errorCode;
        this.params = List.of(param);
    }
    
    public BusinessLogicException(String errorCode, List<String> params) {
        super("Business logic error: " + errorCode + " - " + String.join(", ", params));
        this.errorCode = errorCode;
        this.params = params;
    }
    
    public BusinessLogicException(String errorCode, String param1, String param2) {
        super("Business logic error: " + errorCode + " - " + param1 + ", " + param2);
        this.errorCode = errorCode;
        this.params = List.of(param1, param2);
    }
} 