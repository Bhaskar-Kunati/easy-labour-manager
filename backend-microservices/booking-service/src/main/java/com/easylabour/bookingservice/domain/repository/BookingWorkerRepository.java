package com.easylabour.bookingservice.domain.repository;

import com.easylabour.bookingservice.domain.model.BookingWorker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingWorkerRepository extends JpaRepository<BookingWorker, Long> {
    List<BookingWorker> findByWorkerId(String workerId);
    Optional<BookingWorker> findByBookingIdAndWorkerId(Long bookingId, String workerId);
    List<BookingWorker> findByBookingId(Long bookingId);
}
