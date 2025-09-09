package com.application.booking.dto.templateDto;

import com.application.booking.enumeration.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventsDto {

    private int id;
    private String eventCode;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private int totalSeats;
    private int availableSeats;
    private EventStatus eventStatus;
    private BandsDto band;
}
