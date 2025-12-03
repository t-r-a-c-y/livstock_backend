// src/main/java/com/livestock/dto/response/ApiResponse.java
package com.livestock.dto.response;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private T data;
    private ErrorResponse error;

    @Data
    public static class ErrorResponse {
        private String message;
        private String code;
    }

    // Helper methods for clean code in controllers
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(String message, String code) {
        ApiResponse<T> response = new ApiResponse<>();
        ErrorResponse error = new ErrorResponse();
        error.setMessage(message);
        error.setCode(code);
        response.setError(error);
        return response;
    }
}