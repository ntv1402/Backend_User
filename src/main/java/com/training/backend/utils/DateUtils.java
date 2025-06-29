package com.training.backend.utils;

import com.training.backend.config.MessageConstant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class để xử lý các thao tác liên quan đến ngày tháng
 */
public class DateUtils {

    /**
     * Helper method để parse ngày tháng từ string sang LocalDate
     * 
     * @param dateString chuỗi ngày tháng (format: yyyy/MM/dd)
     * @return LocalDate hoặc null nếu dateString null/empty
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MessageConstant.DATE_FORMAT);
        return LocalDate.parse(dateString, formatter);
    }
} 