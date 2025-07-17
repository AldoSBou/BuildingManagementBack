package com.buildingmanagement.buildingmanagementbackend.common.constans;

public class AppConstants {

    // JWT Constants
    public static final String JWT_SECRET = "mySecretKey";
    public static final int JWT_EXPIRATION = 86400000; // 24 hours

    // API Response Messages
    public static final String SUCCESS_LOGIN = "Login successful";
    public static final String SUCCESS_SIGNUP = "User registered successfully";
    public static final String SUCCESS_LOGOUT = "Logout successful";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid email or password";
    public static final String ERROR_USER_NOT_FOUND = "User not found";
    public static final String ERROR_EMAIL_ALREADY_EXISTS = "Email is already registered";

    // File Upload Constants
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_FILE_EXTENSIONS = {"pdf", "jpg", "jpeg", "png", "doc", "docx"};

    // Pagination Constants
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    private AppConstants() {
        // Private constructor to prevent instantiation
    }
}