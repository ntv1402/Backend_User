package com.training.backend.constant;

public class Constants {

    private Constants() {
    }

    public static final String JWT_SECRET = "ahahaha";
    public static final long JWT_EXPIRATION = 3000;

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
            "userId",
            "fullName",
            "username",
            "email"
    };

}