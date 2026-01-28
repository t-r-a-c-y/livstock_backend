package com.livestock.entity.enums;

// com.livestock.entity.enums.Role.java
public enum Role {
    STAFF,
    VIEWER,
    MANAGER,
    ADMIN;

    // Add this method
    public static Role fromString(String value) {
        if (value == null) return null;
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + value + ". Allowed: STAFF, VIEWER, MANAGER, ADMIN");
        }
    }
}