package com.application.booking.repository;

import com.application.booking.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query(value = "SELECT * FROM bookings WHERE booking_code = :bookingCode", nativeQuery = true)
    Optional<Booking> findByBookingcode(@Param("bookingCode") String bookingCode);

    @Query(value = "SELECT * FROM bookings WHERE user_id = :id", nativeQuery = true)
    Page<Booking> findByUserId(@Param("id") int id, Pageable pageable);

    @Query(value = "SELECT booking_code FROM bookings WHERE booking_code LIKE CONCAT(:prefix, '%') ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<String> findTopByBookingNumberLikeOrderByIdDesc(@Param("prefix") String prefix);
}
