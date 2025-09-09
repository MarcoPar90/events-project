package com.application.events.dto;

import com.application.events.enumeration.EventStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventsDto {

    private int id;
    @NotNull(message = "event code mustn't be null")
    private String eventCode;
    @NotEmpty(message = "title mustn't be empty or null")
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    @Min(value = 100, message = "you must add at least 100 places")
    @NotNull(message = "total seats mustn't be null")
    private int totalSeats;
    @NotNull(message = "available seats seats mustn't be null")
    private int availableSeats;
    private EventStatus eventStatus;
    private BandsDto band;
}
