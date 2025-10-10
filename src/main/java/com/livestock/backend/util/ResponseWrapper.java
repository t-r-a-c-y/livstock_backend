package com.livestock.backend.util;

import lombok.Data;

@Data
public class ResponseWrapper<T> {
    private T data;
    private ErrorMessage error;

    public ResponseWrapper(T data, ErrorMessage error) {
        this.data = data;
        this.error = error;
    }

    @Data
    public static class ErrorMessage {
        private String message;

        public ErrorMessage(String message) {
            this.message = message;
        }
    }
}