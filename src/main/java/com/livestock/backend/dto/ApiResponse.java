package com.livestock.backend.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private T data;
    private ApiError error;

    public ApiResponse() {}

    public ApiResponse(T data, ApiError error) {
        this.data = data;
        this.error = error;
    }

    @Data
    public static class ApiError {
        private String message;

        public ApiError(String message) {
            this.message = message;
        }
    }
}