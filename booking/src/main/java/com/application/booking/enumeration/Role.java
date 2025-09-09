package com.application.booking.enumeration;

public enum Role {
    ADMIN,
    USER;

    public static Role roleFromString(String authority) {
        try {
            return Role.valueOf(authority.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
