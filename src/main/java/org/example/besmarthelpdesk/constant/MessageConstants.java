package org.example.besmarthelpdesk.constant;

public final class MessageConstants {

    private MessageConstants() {
        // Prevent instantiation
    }

    // Success Messages
    public static final String SUCCESS = "success";
    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String LOGOUT_SUCCESS = "Logout successful";
    public static final String REGISTER_SUCCESS = "Member registered successfully";

    // Error Messages
    public static final String EMAIL_REGISTERED = "Email is already registered";
    public static final String MEMBER_NOT_FOUND = "Member not found with ID: ";
    public static final String ACCESS_DENIED = "Access denied";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred";
    public static final String UNAUTHORIZED = "Invalid email or password";
    
    public static final String VALIDATION_ERROR = "Invalid input parameters";
    public static final String SYSTEM_ERROR = "System internal error";
    public static final String NOT_FOUND = "Resource not found";
    public static final String BAD_REQUEST = "Bad request";

    // Validation Messages
    public static final String EMAIL_BLANK = "Email cannot be blank";
    public static final String EMAIL_INVALID = "Email must be a valid email format";
    public static final String PASSWORD_BLANK = "Password cannot be blank";
    public static final String NAME_BLANK = "Name cannot be blank";
    public static final String ROLE_NULL = "Role cannot be null";
}
