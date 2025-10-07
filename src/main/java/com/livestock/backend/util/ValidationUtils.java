package com.livestock.backend.util;


import java.util.UUID;

public class ValidationUtils {

    public static boolean isValidUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Add more validation methods if needed
}
