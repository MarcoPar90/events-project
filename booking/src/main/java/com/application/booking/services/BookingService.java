package com.application.booking.services;

import com.application.booking.dto.BookingDto;
import com.application.booking.dto.PagedResponseDto;
import com.application.booking.dto.SeatsDto;
import com.application.booking.enumeration.BookingStatus;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDto findByBookingNumber(String code, String authorizationHeader);
    BookingDto createBooking(int eventId, String authorizationHeader, SeatsDto seats);
    PagedResponseDto<BookingDto> findBookingsList(int id, String authorizationHeader, Pageable pageable);
    PagedResponseDto<BookingDto> findBookingsListMe(String authorizationHeader, Pageable pageable);
    BookingDto modifyBooking(String code, String authorizationHeader, BookingStatus status);
    void deleteBooking(String bookingCode, String authorizationHeader);
}
