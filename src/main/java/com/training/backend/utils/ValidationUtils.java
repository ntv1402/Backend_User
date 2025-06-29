/*
 * Copyright(C) 2025 Luvina JSC
 * ValidationUtils.java, 17/04/2025 7_nguyenthanhvinh2
 */
package com.training.backend.utils;

import com.training.backend.config.MessageConstant;
import com.training.backend.payload.response.ErrorResponse;
import com.training.backend.payload.request.CertificationRequest;
import com.training.backend.repository.CertificationRepository;
import com.training.backend.repository.DepartmentRepository;
import com.training.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static com.training.backend.config.MessageConstant.*;

/**
 * Utility class cung cấp các phương thức kiểm tra tính hợp lệ của dữ liệu đầu vào
 * Gộp tất cả chức năng validate từ các class cũ và điều chỉnh cho entity User
 * Bao gồm:
 * - Validate dữ liệu form (static methods)
 * - Validate dữ liệu database (instance methods)
 * - Validate tham số danh sách (static methods)
 */
@Component
public class ValidationUtils {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    // ==================== VALIDATE DỮ LIỆU FORM (STATIC METHODS) ====================

    /**
     * Validate username (tên đăng nhập) của user
     * 
     * @param username Username cần kiểm tra
     * @return ErrorResponse nếu có lỗi, null nếu hợp lệ
     */
    public static ErrorResponse validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            return createErrorResponse(ER001_CODE, FIELD_ACCOUNT);
        }
        if (username.length() > 50) {
            return createErrorResponse(ER006_CODE, FIELD_ACCOUNT);
        }
        if (!Pattern.matches("^[a-zA-Z0-9_]+$", username)) {
            return createErrorResponse(ER019_CODE, FIELD_ACCOUNT);
        }
        return null;
    }

    /**
     * Validate fullname (tên đầy đủ) của user
     * 
     * @param fullname Tên đầy đủ cần kiểm tra
     * @return ErrorResponse nếu có lỗi, null nếu hợp lệ
     */
    public static ErrorResponse validateFullname(String fullname) {
        if (fullname == null || fullname.isEmpty()) {
            return createErrorResponse(ER001_CODE, FIELD_NAME);
        }
        if (fullname.length() > 125) {
            return createErrorResponse(ER006_CODE, FIELD_NAME);
        }
        return null;
    }

    /**
     * Validate katakana (tên Kana) của user
     * 
     * @param katakana Tên Kana cần kiểm tra
     * @return ErrorResponse nếu có lỗi, null nếu hợp lệ
     */
    public static ErrorResponse validateKatakana(String katakana) {
        if (katakana == null || katakana.isEmpty()) {
            return createErrorResponse(ER001_CODE, FIELD_KANA_NAME);
        }
        if (katakana.length() > 125) {
            return createErrorResponse(ER006_CODE, FIELD_KANA_NAME);
        }
        if (!Pattern.matches("^[\\uFF65-\\uFF9F\\s]+$", katakana)) {
            return createErrorResponse(ER009_CODE, FIELD_KANA_NAME);
        }
        return null;
    }

    /**
     * Validate birthdate (ngày sinh) của user
     * 
     * @param birthdate Ngày sinh theo định dạng yyyy/MM/dd
     * @return ErrorResponse nếu có lỗi, null nếu hợp lệ
     */
    public static ErrorResponse validateBirthdate(String birthdate) {
        if (birthdate == null || birthdate.isEmpty()) {
            return createErrorResponse(ER001_CODE, FIELD_BIRTH_DATE);
        }
        try {
            LocalDate.parse(birthdate, DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (DateTimeParseException e) {
            return createErrorResponse(ER011_CODE, FIELD_BIRTH_DATE);
        }
        if (!Pattern.matches("^\\d{4}/\\d{2}/\\d{2}$", birthdate)) {
            return createErrorResponse(ER005_CODE, FIELD_BIRTH_DATE, DATE_FORMAT);
        }
        return null;
    }

    /**
     * Validate email của user
     * 
     * @param email Email cần kiểm tra
     * @return ErrorResponse nếu có lỗi, null nếu hợp lệ
     */
    public static ErrorResponse validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return createErrorResponse(ER001_CODE, FIELD_EMAIL);
        }
        if (email.length() > 125) {
            return createErrorResponse(ER006_CODE, FIELD_EMAIL);
        }
        // Kiểm tra format email cơ bản
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email)) {
            return createErrorResponse(ER020_CODE, FIELD_EMAIL);
        }
        return null;
    }

    /**
     * Validate telephone (số điện thoại) của user
     * 
     * @param telephone Số điện thoại cần kiểm tra
     * @return ErrorResponse nếu có lỗi, null nếu hợp lệ
     */
    public static ErrorResponse validateTelephone(String telephone) {
        if (telephone == null || telephone.isEmpty()) {
            return createErrorResponse(ER001_CODE, FIELD_PHONE);
        }
        if (telephone.length() > 50) {
            return createErrorResponse(ER006_CODE, FIELD_PHONE);
        }
        if (!Pattern.matches("^[\\x01-\\x7E]+$", telephone)) {
            return createErrorResponse(ER008_CODE, FIELD_PHONE);
        }
        return null;
    }

    /**
     * Validate password (mật khẩu) của user
     * 
     * @param password Mật khẩu cần kiểm tra
     * @param isNewUser True nếu là thêm mới, false nếu là cập nhật
     * @return ErrorResponse nếu có lỗi, null nếu hợp lệ
     */
    public static ErrorResponse validatePassword(String password, boolean isNewUser) {
        if (isNewUser && (password == null || password.isEmpty())) {
            return createErrorResponse(ER001_CODE, FIELD_PASSWORD);
        }
        if (!isNewUser && (password == null || password.isEmpty())) {
            return null; // Bỏ qua validate khi cập nhật không nhập password
        }
        if (password.length() > 50 || password.length() < 8) {
            return createErrorResponse(ER007_CODE, FIELD_PASSWORD, PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);
        }
        return null;
    }

    /**
     * Validate departmentId (ID phòng ban) của user
     * 
     * @param departmentId ID phòng ban cần kiểm tra
     * @return ErrorResponse nếu có lỗi, null nếu hợp lệ
     */
    public static ErrorResponse validateDepartmentId(Long departmentId) {
        if (departmentId == null) {
            return createErrorResponse(ER002_CODE, FIELD_GROUP);
        }
        if (departmentId <= 0) {
            return createErrorResponse(ER018_CODE, FIELD_GROUP);
        }
        return null;
    }

    /**
     * Validate danh sách certifications
     * 
     * @param certifications Danh sách chứng chỉ cần kiểm tra
     * @return ErrorResponse nếu có lỗi, null nếu hợp lệ
     */
    public static ErrorResponse validateCertifications(List<CertificationRequest> certifications) {
        if (certifications == null || certifications.isEmpty()) {
            return null;
        }

        for (CertificationRequest cert : certifications) {
            // Validate certificationId
            if (cert.getCertificationId() == null) {
                return createErrorResponse(ER001_CODE, FIELD_CERTIFICATION);
            }
            if (cert.getCertificationId() <= 0) {
                return createErrorResponse(ER018_CODE, FIELD_CERTIFICATION);
            }

            // Validate startDate
            if (cert.getCertificationStartDate() == null || cert.getCertificationStartDate().isEmpty()) {
                return createErrorResponse(ER001_CODE, FIELD_CERTIFICATION_START_DATE);
            }
            try {
                LocalDate.parse(cert.getCertificationStartDate(), DateTimeFormatter.ofPattern(DATE_FORMAT));
            } catch (DateTimeParseException e) {
                return createErrorResponse(ER011_CODE, FIELD_CERTIFICATION_START_DATE);
            }
            if (!Pattern.matches("^\\d{4}/\\d{2}/\\d{2}$", cert.getCertificationStartDate())) {
                return createErrorResponse(ER005_CODE, FIELD_CERTIFICATION_START_DATE, DATE_FORMAT);
            }

            // Validate endDate
            if (cert.getCertificationEndDate() == null || cert.getCertificationEndDate().isEmpty()) {
                return createErrorResponse(ER001_CODE, FIELD_CERTIFICATION_END_DATE);
            }
            try {
                LocalDate.parse(cert.getCertificationEndDate(), DateTimeFormatter.ofPattern(DATE_FORMAT));
            } catch (DateTimeParseException e) {
                return createErrorResponse(ER011_CODE, FIELD_CERTIFICATION_END_DATE);
            }
            if (!Pattern.matches("^\\d{4}/\\d{2}/\\d{2}$", cert.getCertificationEndDate())) {
                return createErrorResponse(ER005_CODE, FIELD_CERTIFICATION_END_DATE, DATE_FORMAT);
            }

            // Validate score
            if (cert.getUserCertificationScore() == null) {
                return createErrorResponse(ER001_CODE, FIELD_SCORE);
            }
            if (cert.getUserCertificationScore().compareTo(BigDecimal.ZERO) <= 0) {
                return createErrorResponse(ER018_CODE, FIELD_SCORE);
            }

            // Validate logic: endDate phải sau startDate
            LocalDate startDate = LocalDate.parse(cert.getCertificationStartDate(), DateTimeFormatter.ofPattern(DATE_FORMAT));
            LocalDate endDate = LocalDate.parse(cert.getCertificationEndDate(), DateTimeFormatter.ofPattern(DATE_FORMAT));
            if (!endDate.isAfter(startDate)) {
                return createErrorResponse(ER012_CODE, FIELD_CERTIFICATION_END_DATE);
            }
        }
        return null;
    }

    // ==================== VALIDATE DỮ LIỆU DATABASE (INSTANCE METHODS) ====================

    /**
     * Kiểm tra xem user với userId cho trước có tồn tại trong DB không
     * 
     * @param userId ID của user cần kiểm tra
     * @return true nếu tồn tại, false nếu không tồn tại
     */
    public boolean isUserExists(Long userId) {
        return userRepository.existsById(userId);
    }

    /**
     * Kiểm tra username có bị trùng trong DB không
     * 
     * @param username Username cần kiểm tra
     * @return ErrorResponse nếu bị trùng, null nếu không trùng
     */
    public ErrorResponse checkDuplicateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            ErrorResponse error = new ErrorResponse(API_ERROR);
            error.addMessage(ER003_CODE, Collections.singletonList(FIELD_ACCOUNT));
            return error;
        }
        return null;
    }

    /**
     * Kiểm tra email có bị trùng hay không
     * 
     * @param email Email cần kiểm tra
     * @param userId ID user (dùng khi cập nhật)
     * @param isNewUser true nếu kiểm tra cho user mới, false nếu cập nhật
     * @return ErrorResponse nếu bị trùng, null nếu không trùng
     */
    public ErrorResponse checkDuplicateEmail(String email, Long userId, boolean isNewUser) {
        boolean isDuplicate = isNewUser
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndUserIdNot(email, userId);

        if (isDuplicate) {
            ErrorResponse error = new ErrorResponse(API_ERROR);
            error.addMessage(ER003_CODE, Collections.singletonList(FIELD_EMAIL));
            return error;
        }
        return null;
    }

    /**
     * Kiểm tra department với departmentId có tồn tại trong DB không
     * 
     * @param departmentId ID department cần kiểm tra
     * @return ErrorResponse nếu không tồn tại, null nếu tồn tại
     */
    public ErrorResponse validateDepartmentExists(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            ErrorResponse error = new ErrorResponse(API_ERROR);
            error.addMessage(ER004_CODE, Collections.singletonList(FIELD_GROUP));
            return error;
        }
        return null;
    }

    /**
     * Kiểm tra danh sách certifications có tồn tại trong DB không
     * 
     * @param certifications Danh sách chứng chỉ cần kiểm tra
     * @return ErrorResponse nếu có chứng chỉ không tồn tại, null nếu tất cả hợp lệ
     */
    public ErrorResponse validateCertificationsExist(List<CertificationRequest> certifications) {
        if (certifications != null) {
            for (CertificationRequest cert : certifications) {
                if (!certificationRepository.existsById(cert.getCertificationId())) {
                    ErrorResponse error = new ErrorResponse(API_ERROR);
                    error.addMessage(ER004_CODE, Collections.singletonList(FIELD_CERTIFICATION));
                    return error;
                }
            }
        }
        return null;
    }

    // ==================== VALIDATE THAM SỐ DANH SÁCH (STATIC METHODS) ====================

    /**
     * Kiểm tra giá trị order có nằm trong danh sách cho phép hay không
     * 
     * @param order Chuỗi order cần kiểm tra (ví dụ: ASC, DESC)
     * @return ErrorResponse nếu giá trị không hợp lệ, hoặc null nếu hợp lệ
     */
    public static ErrorResponse validateOrder(String order) {
        if (order != null && !order.isBlank()) {
            String upperOrder = order.toUpperCase();
            if (!upperOrder.equals("ASC") && !upperOrder.equals("DESC")) {
                ErrorResponse errorResponse = new ErrorResponse(API_ERROR);
                errorResponse.addMessage(ER021_CODE, Collections.emptyList());
                return errorResponse;
            }
        }
        return null; // hợp lệ hoặc không truyền order
    }

    /**
     * Kiểm tra tham số kiểu số nguyên dương, thường dùng cho offset hoặc limit trong phân trang
     * 
     * @param number Chuỗi số cần kiểm tra
     * @param param Tên tham số (ví dụ: "offset" hoặc "limit") để trả về lỗi tương ứng
     * @return ErrorResponse nếu không phải số nguyên dương hoặc null nếu hợp lệ
     */
    public static ErrorResponse validatePositiveInteger(String number, String param) {
        try {
            int limitParse = Integer.parseInt(number);
            if (limitParse < 0) {
                throw new NumberFormatException();
            }
            return null; // số nguyên dương hợp lệ
        } catch (NumberFormatException e) {
            ErrorResponse errorResponse = new ErrorResponse(API_ERROR);
            if ("offset".equals(param)) {
                errorResponse.addMessage(ER018_CODE, Collections.singletonList(FIELD_OFFSET));
            } else if ("limit".equals(param)) {
                errorResponse.addMessage(ER018_CODE, Collections.singletonList(FIELD_LIMIT));
            }
            return errorResponse;
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Tạo đối tượng ErrorResponse với 1 tham số
     * 
     * @param code Mã lỗi
     * @param param Tham số mô tả lỗi
     * @return ErrorResponse chứa lỗi và tham số
     */
    private static ErrorResponse createErrorResponse(String code, String param) {
        List<String> params = new ArrayList<>();
        if (param != null) {
            params.add(param);
        }
        ErrorResponse response = new ErrorResponse(API_ERROR);
        response.addMessage(code, params);
        return response;
    }

    /**
     * Tạo ErrorResponse với 2 tham số
     * 
     * @param code Mã lỗi
     * @param param1 Tham số mô tả lỗi 1
     * @param param2 Tham số mô tả lỗi 2
     * @return ErrorResponse chứa lỗi và tham số
     */
    private static ErrorResponse createErrorResponse(String code, String param1, String param2) {
        List<String> params = new ArrayList<>();
        if (param1 != null) params.add(param1);
        if (param2 != null) params.add(param2);
        ErrorResponse response = new ErrorResponse(API_ERROR);
        response.addMessage(code, params);
        return response;
    }

    /**
     * Tạo ErrorResponse với 3 tham số
     * 
     * @param code Mã lỗi
     * @param param1 Tham số mô tả lỗi 1
     * @param param2 Tham số mô tả lỗi 2
     * @param param3 Tham số mô tả lỗi 3
     * @return ErrorResponse chứa lỗi và tham số
     */
    private static ErrorResponse createErrorResponse(String code, String param1, String param2, String param3) {
        List<String> params = new ArrayList<>();
        if (param1 != null) params.add(param1);
        if (param2 != null) params.add(param2);
        if (param3 != null) params.add(param3);
        ErrorResponse response = new ErrorResponse(API_ERROR);
        response.addMessage(code, params);
        return response;
    }
}
