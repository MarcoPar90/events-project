package com.application.booking.enumeration;

import lombok.Getter;

@Getter
public enum BookingStatus {
    CONFIRMED("Confirm"),
    CANCELLED("Cancelled"),
    USER_CANCELLED("Cancelled by user");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

}
