package com.livestock.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String error;   // optional - for error cases

    // Success with data only
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    // Success with data + custom message
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = success(data);
        response.setMessage(message);
        return response;
    }

    // Error response
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setError(message);
        return response;
    }

    // Error with data (e.g. validation errors)
    public static <T> ApiResponse<T> error(String message, T data) {
        ApiResponse<T> response = error(message);
        response.setData(data);
        return response;
    }
}