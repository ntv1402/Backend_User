/*
 * Copyright(C) 2025 Luvina JSC
 * ValidationUtils.java, 17/04/2025 7_nguyenthanhvinh2
 */
package com.training.backend.utils;

import com.training.backend.config.MessageConstant;
import com.training.backend.exception.ValidationException;
import com.training.backend.exception.DuplicateException;
import com.training.backend.exception.BusinessLogicException;
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
import java.util.List;
import java.util.regex.Pattern;

import static com.training.backend.config.MessageConstant.*;

/**
 * Utility class cung cấp các phương thức kiểm tra tính hợp lệ của dữ liệu đầu vào
 * Tổ chức theo từng field để dễ maintain và sử dụng
 * Sử dụng ValidationException thay vì trả về ErrorResponse
 */
@Component
public class ValidationUtils {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    // ==================== VALIDATE FIELD: USERNAME ====================

    /**
     * Validate username (tên đăng nhập) của user
     * 
     * @param username Username cần kiểm tra
     * @param checkDuplicate True nếu cần kiểm tra trùng lặp trong DB, false nếu chỉ validate format
     * @throws ValidationException nếu có lỗi validation format
     * @throws DuplicateException nếu có lỗi trùng lặp
     */
    public void validateUsername(String username, boolean checkDuplicate) {
        // Validate format
        if (username == null || username.isEmpty()) {
            throw new ValidationException(ER001_CODE, FIELD_ACCOUNT);
        }
        if (username.length() > 50) {
            throw new ValidationException(ER006_CODE, FIELD_ACCOUNT);
        }
        if (!Pattern.matches("^[a-zA-Z0-9_]+$", username)) {
            throw new ValidationException(ER019_CODE, FIELD_ACCOUNT);
        }
        
        // Check duplicate nếu cần
        if (checkDuplicate && userRepository.existsByUsername(username)) {
            throw new DuplicateException(ER003_CODE, FIELD_ACCOUNT);
        }
    }

    // ==================== VALIDATE FIELD: FULLNAME ====================

    /**
     * Validate fullname (tên đầy đủ) của user
     * 
     * @param fullname Tên đầy đủ cần kiểm tra
     * @throws ValidationException nếu có lỗi validation
     */
    public void validateFullname(String fullname) {
        if (fullname == null || fullname.isEmpty()) {
            throw new ValidationException(ER001_CODE, FIELD_NAME);
        }
        if (fullname.length() > 125) {
            throw new ValidationException(ER006_CODE, FIELD_NAME);
        }
    }

    // ==================== VALIDATE FIELD: KATAKANA ====================

    /**
     * Validate katakana (tên Kana) của user
     * 
     * @param katakana Tên Kana cần kiểm tra
     * @throws ValidationException nếu có lỗi validation
     */
    public void validateKatakana(String katakana) {
        if (katakana == null || katakana.isEmpty()) {
            throw new ValidationException(ER001_CODE, FIELD_KANA_NAME);
        }
        if (katakana.length() > 125) {
            throw new ValidationException(ER006_CODE, FIELD_KANA_NAME);
        }
        if (!Pattern.matches("^[\\uFF65-\\uFF9F\\s]+$", katakana)) {
            throw new ValidationException(ER009_CODE, FIELD_KANA_NAME);
        }
    }

    // ==================== VALIDATE FIELD: BIRTHDATE ====================

    /**
     * Validate birthdate (ngày sinh) của user
     * 
     * @param birthdate Ngày sinh theo định dạng yyyy/MM/dd
     * @throws ValidationException nếu có lỗi validation
     */
    public void validateBirthdate(String birthdate) {
        if (birthdate == null || birthdate.isEmpty()) {
            throw new ValidationException(ER001_CODE, FIELD_BIRTH_DATE);
        }
        try {
            LocalDate.parse(birthdate, DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (DateTimeParseException e) {
            throw new ValidationException(ER011_CODE, FIELD_BIRTH_DATE);
        }
        if (!Pattern.matches("^\\d{4}/\\d{2}/\\d{2}$", birthdate)) {
            throw new ValidationException(ER005_CODE, FIELD_BIRTH_DATE, DATE_FORMAT);
        }
    }

    // ==================== VALIDATE FIELD: EMAIL ====================

    /**
     * Validate email của user
     * 
     * @param email Email cần kiểm tra
     * @param checkDuplicate True nếu cần kiểm tra trùng lặp trong DB, false nếu chỉ validate format
     * @param userId ID user (dùng khi cập nhật, chỉ cần khi checkDuplicate = true)
     * @param isNewUser true nếu kiểm tra cho user mới, false nếu cập nhật (chỉ cần khi checkDuplicate = true)
     * @throws ValidationException nếu có lỗi validation format
     * @throws DuplicateException nếu có lỗi trùng lặp
     */
    public void validateEmail(String email, boolean checkDuplicate, Long userId, boolean isNewUser) {
        // Validate format
        if (email == null || email.isEmpty()) {
            throw new ValidationException(ER001_CODE, FIELD_EMAIL);
        }
        if (email.length() > 125) {
            throw new ValidationException(ER006_CODE, FIELD_EMAIL);
        }
        // Kiểm tra format email cơ bản
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email)) {
            throw new ValidationException(ER020_CODE, FIELD_EMAIL);
        }
        
        // Check duplicate nếu cần
        if (checkDuplicate) {
            boolean isDuplicate = isNewUser
                    ? userRepository.existsByEmail(email)
                    : userRepository.existsByEmailAndUserIdNot(email, userId);

            if (isDuplicate) {
                throw new DuplicateException(ER003_CODE, FIELD_EMAIL);
            }
        }
    }

    // ==================== VALIDATE FIELD: TELEPHONE ====================

    /**
     * Validate telephone (số điện thoại) của user
     * 
     * @param telephone Số điện thoại cần kiểm tra
     * @throws ValidationException nếu có lỗi validation
     */
    public void validateTelephone(String telephone) {
        if (telephone == null || telephone.isEmpty()) {
            throw new ValidationException(ER001_CODE, FIELD_PHONE);
        }
        if (telephone.length() > 50) {
            throw new ValidationException(ER006_CODE, FIELD_PHONE);
        }
        if (!Pattern.matches("^[\\x01-\\x7E]+$", telephone)) {
            throw new ValidationException(ER008_CODE, FIELD_PHONE);
        }
    }

    // ==================== VALIDATE FIELD: PASSWORD ====================

    /**
     * Validate password (mật khẩu) của user
     * 
     * @param password Mật khẩu cần kiểm tra
     * @param isNewUser True nếu là thêm mới, false nếu là cập nhật
     * @throws ValidationException nếu có lỗi validation
     */
    public void validatePassword(String password, boolean isNewUser) {
        if (isNewUser && (password == null || password.isEmpty())) {
            throw new ValidationException(ER001_CODE, FIELD_PASSWORD);
        }
        if (!isNewUser && (password == null || password.isEmpty())) {
            return; // Bỏ qua validate khi cập nhật không nhập password
        }
        if (password.length() > 50 || password.length() < 8) {
            throw new ValidationException(ER007_CODE, FIELD_PASSWORD, PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);
        }
    }

    // ==================== VALIDATE FIELD: DEPARTMENT ====================

    /**
     * Validate departmentId (ID phòng ban) của user
     * 
     * @param departmentId ID phòng ban cần kiểm tra
     * @param checkExists True nếu cần kiểm tra tồn tại trong DB, false nếu chỉ validate format
     * @throws ValidationException nếu có lỗi validation
     */
    public void validateDepartmentId(Long departmentId, boolean checkExists) {
        // Validate format
        if (departmentId == null) {
            throw new ValidationException(ER002_CODE, FIELD_GROUP);
        }
        if (departmentId <= 0) {
            throw new ValidationException(ER018_CODE, FIELD_GROUP);
        }
        
        // Check exists nếu cần
        if (checkExists && !departmentRepository.existsById(departmentId)) {
            throw new ValidationException(ER004_CODE, FIELD_GROUP);
        }
    }

    // ==================== VALIDATE FIELD: CERTIFICATION ====================

    /**
     * Validate certificationId
     * 
     * @param certificationId ID certification cần kiểm tra
     * @param checkExists True nếu cần kiểm tra tồn tại trong DB, false nếu chỉ validate format
     * @throws ValidationException nếu có lỗi validation
     */
    public void validateCertificationId(Long certificationId, boolean checkExists) {
        // Validate format
        if (certificationId == null) {
            throw new ValidationException(ER001_CODE, FIELD_CERTIFICATION);
        }
        if (certificationId <= 0) {
            throw new ValidationException(ER018_CODE, FIELD_CERTIFICATION);
        }
        
        // Check exists nếu cần
        if (checkExists && !certificationRepository.existsById(certificationId)) {
            throw new ValidationException(ER004_CODE, FIELD_CERTIFICATION);
        }
    }

    /**
     * Validate certification start date
     * 
     * @param startDate Ngày bắt đầu theo định dạng yyyy/MM/dd
     * @throws ValidationException nếu có lỗi validation
     */
    public void validateCertificationStartDate(String startDate) {
        if (startDate == null || startDate.isEmpty()) {
            throw new ValidationException(ER001_CODE, FIELD_CERTIFICATION_START_DATE);
        }
        try {
            LocalDate.parse(startDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (DateTimeParseException e) {
            throw new ValidationException(ER011_CODE, FIELD_CERTIFICATION_START_DATE);
        }
        if (!Pattern.matches("^\\d{4}/\\d{2}/\\d{2}$", startDate)) {
            throw new ValidationException(ER005_CODE, FIELD_CERTIFICATION_START_DATE, DATE_FORMAT);
        }
    }

    /**
     * Validate certification end date
     * 
     * @param endDate Ngày kết thúc theo định dạng yyyy/MM/dd
     * @throws ValidationException nếu có lỗi validation
     */
    public void validateCertificationEndDate(String endDate) {
        if (endDate == null || endDate.isEmpty()) {
            throw new ValidationException(ER001_CODE, FIELD_CERTIFICATION_END_DATE);
        }
        try {
            LocalDate.parse(endDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (DateTimeParseException e) {
            throw new ValidationException(ER011_CODE, FIELD_CERTIFICATION_END_DATE);
        }
        if (!Pattern.matches("^\\d{4}/\\d{2}/\\d{2}$", endDate)) {
            throw new ValidationException(ER005_CODE, FIELD_CERTIFICATION_END_DATE, DATE_FORMAT);
        }
    }

    /**
     * Validate certification score
     * 
     * @param score Điểm certification
     * @throws ValidationException nếu có lỗi validation
     */
    public void validateCertificationScore(BigDecimal score) {
        if (score == null) {
            throw new ValidationException(ER001_CODE, FIELD_SCORE);
        }
        if (score.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(ER018_CODE, FIELD_SCORE);
        }
    }

    /**
     * Validate logic: endDate phải sau startDate
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @throws ValidationException nếu có lỗi format date
     * @throws BusinessLogicException nếu có lỗi logic business
     */
    public void validateCertificationDateLogic(String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
            if (!end.isAfter(start)) {
                throw new BusinessLogicException(ER012_CODE, FIELD_CERTIFICATION_END_DATE);
            }
        } catch (DateTimeParseException e) {
            throw new ValidationException(ER011_CODE, FIELD_CERTIFICATION_END_DATE);
        }
    }

    /**
     * Validate danh sách certifications
     * 
     * @param certifications Danh sách chứng chỉ cần kiểm tra
     * @param checkExists True nếu cần kiểm tra tồn tại trong DB, false nếu chỉ validate format
     * @throws ValidationException nếu có lỗi validation
     */
    public void validateCertifications(List<CertificationRequest> certifications, boolean checkExists) {
        if (certifications == null || certifications.isEmpty()) {
            return;
        }

        for (CertificationRequest cert : certifications) {
            // Validate certificationId
            validateCertificationId(cert.getCertificationId(), checkExists);

            // Validate startDate
            validateCertificationStartDate(cert.getCertificationStartDate());

            // Validate endDate
            validateCertificationEndDate(cert.getCertificationEndDate());

            // Validate score
            validateCertificationScore(cert.getUserCertificationScore());

            // Validate date logic
            validateCertificationDateLogic(
                    cert.getCertificationStartDate(), 
                    cert.getCertificationEndDate()
            );
        }
    }

    // ==================== VALIDATE FIELD: USER ID ====================

    /**
     * Kiểm tra xem user với userId cho trước có tồn tại trong DB không
     * 
     * @param userId ID của user cần kiểm tra
     * @return true nếu tồn tại, false nếu không tồn tại
     */
    public boolean isUserExists(Long userId) {
        return userRepository.existsById(userId);
    }

    // ==================== VALIDATE FIELD: ORDER & PAGINATION ====================

    /**
     * Kiểm tra giá trị order có nằm trong danh sách cho phép hay không
     * 
     * @param order Chuỗi order cần kiểm tra (ví dụ: ASC, DESC)
     * @throws ValidationException nếu giá trị không hợp lệ
     */
    public void validateOrder(String order, String field) {
        if (order != null && !order.isBlank()) {
            String upperOrder = order.toUpperCase();
            if (!upperOrder.equals("ASC") && !upperOrder.equals("DESC")) {
                throw new ValidationException(ER021_CODE, field);
            }
        }
    }

    /**
     * Kiểm tra tham số kiểu số nguyên dương, thường dùng cho offset hoặc limit trong phân trang
     * 
     * @param number Chuỗi số cần kiểm tra
     * @param param Tên tham số (ví dụ: "offset" hoặc "limit") để trả về lỗi tương ứng
     * @throws ValidationException nếu không phải số nguyên dương
     */
    public void validatePositiveInteger(String number, String param) {
        try {
            int limitParse = Integer.parseInt(number);
            if (limitParse < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            if ("offset".equals(param)) {
                throw new ValidationException(ER018_CODE, FIELD_OFFSET);
            } else if ("limit".equals(param)) {
                throw new ValidationException(ER018_CODE, FIELD_LIMIT);
            }
        }
    }
}
