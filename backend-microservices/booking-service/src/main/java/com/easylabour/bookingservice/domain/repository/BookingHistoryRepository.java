package com.easylabour.bookingservice.domain.repository;

import com.easylabour.bookingservice.domain.model.BookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingHistoryRepository extends JpaRepository<BookingHistory, Long> {
    List<BookingHistory> findByBookingIdOrderByTimestampDesc(String bookingId);
}
