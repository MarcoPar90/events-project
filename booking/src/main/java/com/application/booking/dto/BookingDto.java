package com.application.booking.dto;

import com.application.booking.dto.templateDto.EventsDto;
import com.application.booking.dto.templateDto.UserDto;
import com.application.booking.enumeration.BookingStatus;
import lombok.Data;

@Data
public class BookingDto {

    private int id;
    private int seats;
    private BookingStatus bookingStatus;
    private String bookingCode;
    private UserDto user;
    private EventsDto event;
    private String cancelledMessage;
}
