// src/main/java/com/livestock/dto/response/ApiResponse.java
package com.livestock.dto.response;

public class ApiResponse<T> {

    private T data;
    private ErrorResponse error;

    // --- Constructors ---
    public ApiResponse() {}

    public ApiResponse(T data) {
        this.data = data;
    }

    public ApiResponse(ErrorResponse error) {
        this.error = error;
    }

    // --- Getters and Setters ---
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }

    // --- Inner ErrorResponse class with manual getters/setters ---
    public static class ErrorResponse {
        private String message;
        private String code;

        public ErrorResponse() {}

        public ErrorResponse(String message, String code) {
            this.message = message;
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    // --- Static helper methods (used in controllers) ---
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