/*
 * Copyright(C) 2025 Luvina JSC
 * NotFoundException.java, 17/04/2025 7_nguyenthanhvinh2
 */
package com.training.backend.exception;

import lombok.Getter;

import java.util.List;

/**
 * Exception được throw khi không tìm thấy tài nguyên
 */
@Getter
public class NotFoundException extends RuntimeException {
    
    private final String errorCode;
    private final List<String> params;
    
    public NotFoundException(String errorCode, String param) {
        super("Not found error: " + errorCode + " - " + param);
        this.errorCode = errorCode;
        this.params = List.of(param);
    }
    
    public NotFoundException(String errorCode, List<String> params) {
        super("Not found error: " + errorCode + " - " + String.join(", ", params));
        this.errorCode = errorCode;
        this.params = params;
    }
    
    // Constructor cũ để backward compatibility
    public NotFoundException(String message) {
        super(message);
        this.errorCode = "ER004";
        this.params = List.of("resource");
    }
}
