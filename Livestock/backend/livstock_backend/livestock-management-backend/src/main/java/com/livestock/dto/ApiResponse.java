package com.livestock.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Standardized API response wrapper for all endpoints.
 * Supports both success and error cases with consistent structure.
 */
@Data
public class ApiResponse<T> {

    private boolean success;
    private String message;           // Human-readable message (success or error explanation)
    private T data;                   // The actual response payload (null on error)
    private String error;             // Detailed error code or message (optional)
    private String errorCode;         // Optional structured error code (e.g., "DUPLICATE_EMAIL", "INVALID_TOKEN")
    private LocalDateTime timestamp;  // When this response was generated

    // Default constructor sets timestamp
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // ───────────────────────────────────────────────────────────────
    //                Static factory methods (fluent API)
    // ───────────────────────────────────────────────────────────────

    /**
     * Success response with data only
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setMessage("Operation successful");
        return response;
    }

    /**
     * Success response with custom message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = success(data);
        response.setMessage(message);
        return response;
    }

    /**
     * Simple error response (message only)
     */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage("An error occurred");
        response.setError(message);
        response.setErrorCode("GENERAL_ERROR");
        return response;
    }

    /**
     * Error with specific error code and message
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        ApiResponse<T> response = error(message);
        response.setErrorCode(errorCode);
        return response;
    }

    /**
     * Error with additional data (e.g. validation errors map)
     */
    public static <T> ApiResponse<T> error(String message, T errorData) {
        ApiResponse<T> response = error(message);
        response.setData(errorData);
        return response;
    }

    /**
     * Error with full control (message, code, data)
     */
    public static <T> ApiResponse<T> error(String message, String errorCode, T errorData) {
        ApiResponse<T> response = error(message, errorCode);
        response.setData(errorData);
        return response;
    }

    // ───────────────────────────────────────────────────────────────
    //                  Convenience instance methods
    // ───────────────────────────────────────────────────────────────

    /**
     * Chainable setter for message
     */
    public ApiResponse<T> withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Chainable setter for error code
     */
    public ApiResponse<T> withErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    /**
     * Quick check if this is a success response
     */
    public boolean isSuccess() {
        return success;
    }
}