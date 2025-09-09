package com.application.booking.services.impl;

import com.application.booking.dto.BookingDto;
import com.application.booking.dto.PagedResponseDto;
import com.application.booking.dto.templateDto.EventsDto;
import com.application.booking.dto.SeatsDto;
import com.application.booking.dto.templateDto.UserDto;
import com.application.booking.entity.Booking;
import com.application.booking.enumeration.BookingStatus;
import com.application.booking.enumeration.Role;
import com.application.booking.exception.BadRequestException;
import com.application.booking.exception.NotFoundException;
import com.application.booking.exception.UnauthorizedException;
import com.application.booking.repository.BookingRepository;
import com.application.booking.services.BookingService;
import com.application.booking.utils.Conversions;
import com.application.booking.utils.JwtUtilities;
import com.application.booking.utils.RestTemplateApis;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final Conversions conversions;
    private final RestTemplateApis restTemplateApis;
    private final JwtUtilities jwtUtilities;

    @Override
    public BookingDto findByBookingNumber(String bookingCode, String authorizationHeader) {
        Booking code = bookingRepository.findByBookingcode(bookingCode)
                .orElseThrow(() -> {
                    log.warning("****** Booking " + bookingCode + " not found ******");
                    return new NotFoundException("Booking " + bookingCode + " not found");
                });

        UserDto userDto = restTemplateApis.getUser(authorizationHeader, "me");
        EventsDto eventsDto = restTemplateApis.getEvents(authorizationHeader, code.getEventId());

        BookingDto bookingDto = conversions.convertBookingsEntityToDto(code);
        bookingDto.setEvent(eventsDto);
        bookingDto.setUser(userDto);

        if(BookingStatus.CANCELLED.equals(code.getBookingStatus())) {
            bookingDto.setCancelledMessage("We apologize, but the event has been canceled. The refund will occur within 30 business days.");
        }

        if(BookingStatus.USER_CANCELLED.equals(code.getBookingStatus())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = code.getUpdatedAt().format(formatter);
            bookingDto.setCancelledMessage(String.format("You cancel booking in date %s", formattedDate));
        }

        log.info("booking " + bookingDto.getBookingCode() + " found");

        return bookingDto;
    }

    @Override
    @Transactional
    public BookingDto createBooking(int eventId, String authorizationHeader, SeatsDto seats) {
        EventsDto event = restTemplateApis.getEvents(authorizationHeader, eventId);
        UserDto userDto = restTemplateApis.getUser(authorizationHeader, "me");

        validateSeats(event, seats);

        event.setAvailableSeats(event.getAvailableSeats() - seats.getSeats());
        EventsDto updatedEvent = restTemplateApis.modifyEvent(authorizationHeader, eventId, event);

        String bookingCode = generateBookingCode(updatedEvent.getEventCode());

        Booking booking = new Booking();
        booking.setEventId(updatedEvent.getId());
        booking.setUserId(userDto.getId());
        booking.setSeats(seats.getSeats());
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setBookingCode(bookingCode);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        BookingDto bookingDto = conversions.convertBookingsEntityToDto(savedBooking);
        bookingDto.setEvent(event);
        bookingDto.setUser(userDto);

        return bookingDto;
    }

    private void validateSeats(EventsDto event, SeatsDto seats) {
        if (event.getAvailableSeats() == 0) {
            throw new BadRequestException("No available seats");
        }
        if (event.getAvailableSeats() < seats.getSeats()) {
            throw new BadRequestException("Number of selected seats not available");
        }
    }

    private String generateBookingCode(String codePrefix) {
        String lastBooking = bookingRepository
                .findTopByBookingNumberLikeOrderByIdDesc(codePrefix + "-%")
                .orElse(null);

        if (lastBooking == null) {
            return codePrefix + "-00001";
        }

        String[] parts = lastBooking.split("-");
        if (parts.length != 2) {
            throw new IllegalStateException("Invalid booking format: " + lastBooking);
        }

        String numberPart = parts[1];
        int length = numberPart.length();
        int number = Integer.parseInt(numberPart) + 1;

        return parts[0] + "-" + String.format("%0" + length + "d", number);

    }

    @Override
    public PagedResponseDto<BookingDto> findBookingsList(int userId, String authorizationHeader, Pageable pageable) {

        Page<Booking> bookingList = bookingRepository.findByUserId(userId, pageable);

        UserDto userDto = restTemplateApis.getUser(authorizationHeader, Integer.toString(userId));

        List<Integer> eventIds = bookingList.getContent().stream()
                .map(Booking::getEventId)
                .distinct()
                .collect(Collectors.toList());

        List<EventsDto> events = restTemplateApis.getEventsList(authorizationHeader, eventIds);

        Map<Integer, EventsDto> eventsMap = events.stream()
                .collect(Collectors.toMap(EventsDto::getId, e->e));

        PagedResponseDto<BookingDto> response = new PagedResponseDto<>();
        response.setData(bookingList.getContent()
                .stream()
                .map(booking -> {
                    BookingDto dto = conversions.convertBookingsEntityToDto(booking);
                    dto.setUser(userDto);
                    dto.setEvent(eventsMap.get(booking.getEventId()));
                    return dto;
                })
                .collect(Collectors.toList())
        );
        response.setTotalPages(bookingList.getTotalPages());
        response.setTotalRecords(bookingList.getTotalElements());

        return response;
    }

    @Override
    public PagedResponseDto<BookingDto> findBookingsListMe(String authorizationHeader, Pageable pageable) {
        UserDto userDto = restTemplateApis.getUser(authorizationHeader, "me");

        Page<Booking> bookingList = bookingRepository.findByUserId(userDto.getId(), pageable);

        List<Integer> eventIds = bookingList.getContent()
                .stream()
                .map(Booking::getEventId)
                .distinct()
                .collect(Collectors.toList());

        List<EventsDto> events = restTemplateApis.getEventsList(authorizationHeader, eventIds);

        Map<Integer, EventsDto> eventsMap = events.stream()
                .collect(Collectors.toMap(EventsDto::getId, e -> e));

        PagedResponseDto<BookingDto> response = new PagedResponseDto<>();
        response.setData(bookingList.getContent()
                .stream()
                .map(booking -> {
                    BookingDto dto = conversions.convertBookingsEntityToDto(booking);
                    dto.setUser(userDto);
                    dto.setEvent(eventsMap.get(booking.getEventId()));
                    return dto;
                })
                .collect(Collectors.toList()));
        response.setTotalPages(bookingList.getTotalPages());
        response.setTotalRecords(bookingList.getTotalElements());

        return response;
    }

    @Override
    @Transactional
    public BookingDto modifyBooking(String bookingCode, String authorizationHeader, BookingStatus status) {
        Booking booking = bookingRepository.findByBookingcode(bookingCode)
                .orElseThrow(() -> {
                    log.warning("****** Booking " + bookingCode + " not found ******");
                    return new NotFoundException("Booking " + bookingCode + " not found");
                });

        booking.setBookingStatus(status);
        bookingRepository.save(booking);

        UserDto userDto = restTemplateApis.getUser(authorizationHeader, "me");
        EventsDto eventsDto = restTemplateApis.getEvents(authorizationHeader, booking.getEventId());

        BookingDto bookingDto = conversions.convertBookingsEntityToDto(booking);

        if(BookingStatus.USER_CANCELLED.equals(status) || BookingStatus.CANCELLED.equals(status)) {
            eventsDto.setAvailableSeats(eventsDto.getAvailableSeats() + bookingDto.getSeats());
            eventsDto = restTemplateApis.modifyEvent(authorizationHeader, bookingDto.getEvent().getId(), eventsDto);
        }
        bookingDto.setUser(userDto);
        bookingDto.setEvent(eventsDto);

        if(BookingStatus.USER_CANCELLED.equals(status)) {
            bookingDto.setCancelledMessage("Your booking has been successfully canceled");
        }

        return bookingDto;
    }

    @Override
    public void deleteBooking(String bookingCode, String authorizationHeader) {
        Booking booking = bookingRepository.findByBookingcode(bookingCode)
                .orElseThrow(
                        () -> {
                            log.warning("****** Booking " + bookingCode + " not found ******");
                            return new NotFoundException("Booking " + bookingCode + " not found");
                        }
                );
        UserDto userDto = restTemplateApis.getUser(authorizationHeader, String.valueOf(booking.getUserId()));

        String tokenEmail = jwtUtilities.getClaims(authorizationHeader).getSubject();
        String tokenRole = jwtUtilities.getClaims(authorizationHeader)
                .get("authorities", List.class)
                .getFirst().toString().substring(5);
        try {
            // a normal user cannot delete another user's booking
            if(userDto.getEmail().equals(tokenEmail) || Role.ADMIN.name().equals(tokenRole)) {
                bookingRepository.delete(booking);
            } else {
                throw new UnauthorizedException("User not authorized");
            }

        } catch (Exception e) {
            log.severe("Unexpected error deleting event: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
