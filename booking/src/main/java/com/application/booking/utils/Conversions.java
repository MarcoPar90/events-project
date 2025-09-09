package com.application.booking.utils;

import com.application.booking.dto.BookingDto;
import com.application.booking.entity.Booking;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Conversions {
    private final ModelMapper modelMapper;

    public BookingDto convertBookingsEntityToDto(Booking booking) {
        return modelMapper.map(booking, BookingDto.class);
    }

    public Booking convertBookingsDtoToEntity(BookingDto bookingDto) {
        return modelMapper.map(bookingDto, Booking.class);
    }
}
