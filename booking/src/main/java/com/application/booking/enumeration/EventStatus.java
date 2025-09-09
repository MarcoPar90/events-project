package com.application.booking.enumeration;

import lombok.Getter;

@Getter
public enum EventStatus {
    CONFIRMED("Confirm"),
    CANCELLED("Cancelled");

    private final String displayName;

    EventStatus(String displayName) {
        this.displayName = displayName;
    }
}
