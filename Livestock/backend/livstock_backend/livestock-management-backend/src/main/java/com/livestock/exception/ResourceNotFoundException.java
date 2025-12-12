// src/main/java/com/livestock/exception/ResourceNotFoundException.java
package com.livestock.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}