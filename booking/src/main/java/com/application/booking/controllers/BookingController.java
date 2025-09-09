package com.application.booking.controllers;

import com.application.booking.dto.BookingDto;
import com.application.booking.dto.PagedResponseDto;
import com.application.booking.dto.SeatsDto;
import com.application.booking.enumeration.BookingStatus;
import com.application.booking.exception.ErrorMessage;
import com.application.booking.services.impl.BookingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/booking")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Log
public class BookingController {

    private final BookingServiceImpl bookingService;

    @GetMapping("/{bookingCode}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return booking", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BookingDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Booking not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Search booking by booking code")
    public ResponseEntity<BookingDto> getBookingByCode(@PathVariable("bookingCode") String bookingCode,
                                                       @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {
        log.info("****** Start to check for booking with code " + bookingCode + " ******");
        BookingDto booking = bookingService.findByBookingNumber(bookingCode, authorizationHeader);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @GetMapping("/list/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return booking list", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BookingDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - User not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Return user booking's list")
    public ResponseEntity<PagedResponseDto<BookingDto>> getUserBookingsList(@PathVariable("userId") int userId,
                                                                            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
                                                                            @ParameterObject Pageable pageable) {
        log.info("****** Start to check bookings list of user " + userId + " ******");
        PagedResponseDto<BookingDto> booking = bookingService.findBookingsList(userId, authorizationHeader, pageable);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @GetMapping("/list/me")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return booking list", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BookingDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - User not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
    })
    @Operation(summary = "Return logged user booking's list")
    public ResponseEntity<PagedResponseDto<BookingDto>> getUserBookingsListMe(@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
                                                                            @ParameterObject Pageable pageable) {
        log.info("****** Start to check token user bookings list ******");
        PagedResponseDto<BookingDto> booking = bookingService.findBookingsListMe(authorizationHeader, pageable);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @PostMapping("/{eventId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created successfully", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BookingDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request - Some required fields is missing within the body", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - User or event not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)))
    })
    @Operation(summary = "Create booking")
    public ResponseEntity<BookingDto> createBooking(@PathVariable("eventId") int eventId,
                                                    @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
                                                    @RequestBody SeatsDto seats
                                                    ) {
        log.info("****** Start get booking for event " + eventId + " ******");
        BookingDto booking = bookingService.createBooking(eventId, authorizationHeader, seats);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingCode}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking modified successfully", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BookingDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request - Some required fields is missing within the body", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Booking not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)))
    })
    @Operation(summary = "Modify booking")
    public ResponseEntity<BookingDto> modifyBooking(@PathVariable("bookingCode") String bookingCode,
                                                    @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
                                                    @RequestBody BookingStatus status) {
        log.info("****** Start to change booking with code " + bookingCode + " ******");
        BookingDto bookingDto = bookingService.modifyBooking(bookingCode, authorizationHeader, status);
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @DeleteMapping("/{bookingCode}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking modified successfully", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = BookingDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request - Some required fields is missing within the body", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authorized or Token expired", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Not Found - Booking not found", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)))
    })
    @Operation(summary = "Delete a booking")
    public ResponseEntity<Map<String, String>> deleteBooking(@PathVariable("bookingCode") String bookingCode,
                                                             @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader) {
        log.info("****** Start to change booking with code " + bookingCode + " ******");
        Map<String, String> response = new HashMap<>();

        bookingService.deleteBooking(bookingCode, authorizationHeader);

        response.put("message", "Deleted booking " + bookingCode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
