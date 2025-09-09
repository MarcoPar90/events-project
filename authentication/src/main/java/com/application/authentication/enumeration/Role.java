package com.application.authentication.enumeration;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ADMIN"),
    USER("USER");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }
}
