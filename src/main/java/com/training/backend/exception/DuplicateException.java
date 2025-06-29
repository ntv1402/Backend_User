/*
 * Copyright(C) 2025 Luvina JSC
 * DuplicateException.java, 17/04/2025 7_nguyenthanhvinh2
 */
package com.training.backend.exception;

import lombok.Getter;

import java.util.List;

/**
 * Exception được throw khi có lỗi trùng lặp dữ liệu
 */
@Getter
public class DuplicateException extends RuntimeException {
    
    private final String errorCode;
    private final List<String> params;
    
    public DuplicateException(String errorCode, String param) {
        super("Duplicate error: " + errorCode + " - " + param);
        this.errorCode = errorCode;
        this.params = List.of(param);
    }
    
    public DuplicateException(String errorCode, List<String> params) {
        super("Duplicate error: " + errorCode + " - " + String.join(", ", params));
        this.errorCode = errorCode;
        this.params = params;
    }
} 