// src/main/java/com/livestock/exception/UnauthorizedException.java
package com.livestock.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}