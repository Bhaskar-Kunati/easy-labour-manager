package com.easylabour.bookingservice.application.service;

import com.easylabour.bookingservice.domain.model.BookingOutboxEvent;
import com.easylabour.bookingservice.domain.repository.BookingOutboxRepository;
import com.easylabour.bookingservice.infrastructure.client.WalletClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingOutboxProcessor {

    private final BookingOutboxRepository outboxRepository;
    private final WalletClient walletClient;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutboxEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<BookingOutboxEvent> pendingEvents = outboxRepository.findPendingEventsForProcessingWithLock(now);

        for (BookingOutboxEvent event : pendingEvents) {
            try {
                walletClient.creditWallet(
                        event.getWorkerId(),
                        event.getAmount(),
                        event.getReferenceId(),
                        "Earnings for booking " + event.getBookingId()
                );

                event.setStatus("COMPLETED");
                event.setLastError(null);
                outboxRepository.save(event);
                log.info("Successfully processed outbox event ID {} with reference {}", event.getId(), event.getReferenceId());
            } catch (SecurityException e) {
                // 401/403 security/configuration failure - do not blind retry endlessly
                log.error("Security/auth failure processing outbox event ID {}: {}", event.getId(), e.getMessage());
                event.setStatus("FAILED");
                event.setLastError(e.getMessage());
                outboxRepository.save(event);
            } catch (Exception e) {
                // Transient error - retry with exponential backoff
                int retryCount = event.getRetryCount() + 1;
                event.setRetryCount(retryCount);
                long backoffSeconds = (long) Math.min(300, Math.pow(2, retryCount) * 5);
                event.setNextRetryTime(LocalDateTime.now().plusSeconds(backoffSeconds));
                event.setLastError(e.getMessage());
                outboxRepository.save(event);
                log.warn("Transient failure processing outbox event ID {} (retry count {}): {}", event.getId(), retryCount, e.getMessage());
            }
        }
    }
}
