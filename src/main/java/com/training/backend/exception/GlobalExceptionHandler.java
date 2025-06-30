/*
 * Copyright(C) 2025 Luvina JSC
 * GlobalExceptionHandler.java, 17/04/2025 7_nguyenthanhvinh2
 */
package com.training.backend.exception;

import com.training.backend.constant.MessageConstant;
import com.training.backend.payload.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.training.backend.constant.MessageConstant.API_ERROR;

/**
 * Global Exception Handler để xử lý các exception và trả về response phù hợp
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý ValidationException và chuyển đổi thành ErrorResponse
     * Status: 400 Bad Request - Dữ liệu đầu vào không hợp lệ
     * 
     * @param ex ValidationException được throw
     * @return ResponseEntity chứa ErrorResponse
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        log.warn("Validation error: {} - {}", ex.getErrorCode(), ex.getParams());
        
        ErrorResponse errorResponse = new ErrorResponse(API_ERROR);
        errorResponse.addMessage(ex.getErrorCode(), ex.getParams());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Xử lý DuplicateException và chuyển đổi thành ErrorResponse
     * Status: 409 Conflict - Dữ liệu bị trùng lặp
     * 
     * @param ex DuplicateException được throw
     * @return ResponseEntity chứa ErrorResponse
     */
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateException(DuplicateException ex) {
        log.warn("Duplicate error: {} - {}", ex.getErrorCode(), ex.getParams());
        
        ErrorResponse errorResponse = new ErrorResponse(API_ERROR);
        errorResponse.addMessage(ex.getErrorCode(), ex.getParams());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Xử lý BusinessLogicException và chuyển đổi thành ErrorResponse
     * Status: 422 Unprocessable Entity - Dữ liệu hợp lệ nhưng vi phạm business logic
     * 
     * @param ex BusinessLogicException được throw
     * @return ResponseEntity chứa ErrorResponse
     */
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(BusinessLogicException ex) {
        log.warn("Business logic error: {} - {}", ex.getErrorCode(), ex.getParams());
        
        ErrorResponse errorResponse = new ErrorResponse(API_ERROR);
        errorResponse.addMessage(ex.getErrorCode(), ex.getParams());
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /**
     * Xử lý NotFoundException
     * Status: 404 Not Found - Không tìm thấy tài nguyên
     * 
     * @param ex NotFoundException được throw
     * @return ResponseEntity chứa ErrorResponse
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        log.warn("Not found error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(API_ERROR);
        errorResponse.addMessage(MessageConstant.ER004_CODE, ex.getParams());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Xử lý RuntimeException
     * Status: 500 Internal Server Error - Lỗi hệ thống
     * 
     * @param ex RuntimeException được throw
     * @return ResponseEntity chứa ErrorResponse
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime error occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(API_ERROR);
        errorResponse.addMessage(MessageConstant.ER013_CODE, null);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Xử lý các exception khác chưa được xử lý
     * Status: 500 Internal Server Error - Lỗi không xác định
     * 
     * @param ex Exception được throw
     * @return ResponseEntity chứa ErrorResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(API_ERROR);
        errorResponse.addMessage(MessageConstant.ER013_CODE, null);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
} 