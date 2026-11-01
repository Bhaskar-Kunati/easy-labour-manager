package com.easylabour.bookingservice.domain.repository;

import com.easylabour.bookingservice.domain.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingNumber(String bookingNumber);
    List<Booking> findByCustomerId(String customerId);
    Optional<Booking> findByIdempotencyKey(String idempotencyKey);
}
