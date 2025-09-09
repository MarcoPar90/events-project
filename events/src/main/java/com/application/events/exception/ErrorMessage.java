package com.application.events.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorMessage {

    public String error;
    public String message;
    public LocalDateTime timestamp;
    public int status;
}
