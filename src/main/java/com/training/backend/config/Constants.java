package com.training.backend.config;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class Constants {

    private Constants() {
    }

    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final boolean IS_CROSS_ALLOW = true;

    public static final String JWT_SECRET = "Luvina-Academe";
    public static final long JWT_EXPIRATION = 160 * 60 * 60; // 7 day

    // config endpoints public
    public static final String[] ENDPOINTS_PUBLIC = new String[] {
            "/",
            "/login/**",
            "/error/**"
    };

    // config endpoints for USER role
    public static final String[] ENDPOINTS_WITH_ROLE = new String[] {
            "/user/**"
    };

    // user attributies put to token
    public static final String[] ATTRIBUTIES_TO_TOKEN = new String[] {
            "employeeId",
            "employeeName",
            "employeeLoginId",
            "employeeEmail"
    };
    public static final String REQUEST_PARAM_EMPLOYEE_NAME = "employee_name";
    public static final String REQUEST_PARAM_DEPARTMENT_ID = "department_id";
    public static final String REQUEST_PARAM_ORD_EMPLOYEE_NAME = "ord_employee_name";
    public static final String REQUEST_PARAM_ORD_CERTIFICATION_NAME = "ord_certification_name";
    public static final String REQUEST_PARAM_ORD_END_DATE = "ord_end_date";
    public static final String REQUEST_PARAM_OFFSET = "offset";
    public static final String REQUEST_PARAM_LIMIT = "limit";

    public static final String DEFAULT_VALUE_OFFSET = "0";
    public static final String DEFAULT_VALUE_LIMIT = "5";
    public static final List<String> ALLOWED_ORDER = List.of("ASC", "DESC");

}