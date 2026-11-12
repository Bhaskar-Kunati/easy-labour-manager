package com.easylabour.bookingservice.domain.repository;

import com.easylabour.bookingservice.domain.model.BookingOutboxEvent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingOutboxRepository extends JpaRepository<BookingOutboxEvent, Long> {
    Optional<BookingOutboxEvent> findByReferenceId(String referenceId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM BookingOutboxEvent e WHERE e.status = 'PENDING' AND e.nextRetryTime <= :now ORDER BY e.createdAt ASC")
    List<BookingOutboxEvent> findPendingEventsForProcessingWithLock(@Param("now") LocalDateTime now);
}
