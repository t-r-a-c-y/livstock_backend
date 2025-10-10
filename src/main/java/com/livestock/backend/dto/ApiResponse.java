package com.livestockmanagement.backend.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private T data;
    private Error error;

    @Data
    public static class Error {
        private String message;
    }
}