package com.easylabour.bookingservice.application.service;

import com.easylabour.bookingservice.domain.model.Booking;
import com.easylabour.bookingservice.domain.model.BookingOutboxEvent;
import com.easylabour.bookingservice.domain.model.BookingStatus;
import com.easylabour.bookingservice.domain.model.BookingWorker;
import com.easylabour.bookingservice.domain.model.BookingWorkerStatus;
import com.easylabour.bookingservice.domain.repository.BookingOutboxRepository;
import com.easylabour.bookingservice.domain.repository.BookingRepository;
import com.easylabour.bookingservice.domain.repository.BookingWorkerRepository;
import com.easylabour.bookingservice.infrastructure.kafka.BookingEventProducer;
import com.easylabour.bookingservice.web.dto.BookingRequest;
import com.easylabour.bookingservice.web.dto.CatalogServiceDto;
import com.easylabour.common.event.BookingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingWorkerRepository bookingWorkerRepository;
    private final BookingOutboxRepository bookingOutboxRepository;
    private final WorkerAvailabilityService workerAvailabilityService;
    private final CouponService couponService;
    private final BookingEventProducer eventProducer;
    private final RestTemplate restTemplate;

    @Value("${catalog.service.url:http://localhost:8080}")
    private String catalogServiceUrl;

    @Value("${user.service.url:http://localhost:8082}")
    private String userServiceUrl;

    @Transactional
    public Booking createBooking(String customerId, String idempotencyKey, BookingRequest request) {
        if (request.getServiceDateTime() == null || request.getServiceDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service date and time must be in the future");
        }

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<Booking> existingOpt = bookingRepository.findByIdempotencyKey(idempotencyKey);
            if (existingOpt.isPresent()) {
                Booking existing = existingOpt.get();
                boolean matches = existing.getServiceId().equals(request.getServiceId())
                        && existing.getServiceDateTime().equals(request.getServiceDateTime())
                        && existing.getLocation().equals(request.getLocation());
                if (matches) {
                    return existing;
                } else {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Idempotency key reused with different booking payload");
                }
            }
        }

        CatalogServiceDto catalogService = fetchCatalogService(request.getServiceId());
        if (catalogService == null || !"ACTIVE".equalsIgnoreCase(catalogService.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or inactive service ID");
        }

        BigDecimal basePrice = catalogService.getBasePrice();

        Booking booking = Booking.builder()
                .customerId(customerId)
                .serviceId(request.getServiceId())
                .serviceName(catalogService.getName())
                .bookedPrice(basePrice)
                .discount(BigDecimal.ZERO)
                .totalAmount(basePrice)
                .status(BookingStatus.PENDING)
                .serviceDateTime(request.getServiceDateTime())
                .location(request.getLocation())
                .idempotencyKey(idempotencyKey)
                .build();

        Booking saved = bookingRepository.save(booking);

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            BigDecimal discount = couponService.applyAndRecordCoupon(request.getCouponCode(), customerId, basePrice, saved.getId());
            saved.setDiscount(discount);
            saved.setTotalAmount(basePrice.subtract(discount));
            saved = bookingRepository.save(saved);
        }

        publishEvent(saved, "BOOKING_CREATED");
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Booking> getCustomerBookings(String customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public Booking getBookingDetails(Long bookingId, String customerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        
        if (!booking.getCustomerId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to booking");
        }
        enrichWorkerSummary(booking);
        return booking;
    }

    @Transactional
    public Booking cancelBooking(Long bookingId, String customerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!booking.getCustomerId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking cannot be cancelled in status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);
        publishEvent(updated, "BOOKING_CANCELLED");
        return updated;
    }

    @Transactional
    public void cancelBookingById(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        publishEvent(booking, "BOOKING_CANCELLED");
    }

    @Transactional
    public void updateStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        booking.setStatus(status);
        bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<Booking> getAssignedBookings(String workerId) {
        List<BookingWorker> workers = bookingWorkerRepository.findByWorkerId(workerId);
        return workers.stream()
                .map(BookingWorker::getBooking)
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    public Booking getWorkerBookingDetails(Long bookingId, String workerId) {
        BookingWorker assignment = bookingWorkerRepository.findByBookingIdAndWorkerId(bookingId, workerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to booking"));
        return assignment.getBooking();
    }

    @Transactional
    public Booking completeBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.FAILED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot complete a cancelled or failed booking");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.SERVICE_COMPLETED) {
            return booking;
        }

        List<BookingWorker> workers = bookingWorkerRepository.findByBookingId(bookingId);
        if (workers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot complete booking without an assigned worker");
        }

        BookingWorker workerAssignment = workers.get(0);
        String trustedWorkerId = workerAssignment.getWorkerId();
        BigDecimal payoutAmount = booking.getTotalAmount();

        booking.setStatus(BookingStatus.COMPLETED);
        Booking updatedBooking = bookingRepository.save(booking);

        workerAssignment.setStatus(BookingWorkerStatus.SERVICE_COMPLETED);
        workerAssignment.setServiceCompletedAt(LocalDateTime.now());
        workerAssignment.setEarnings(payoutAmount);
        bookingWorkerRepository.save(workerAssignment);

        String referenceId = "BOOKING_EARNINGS_" + booking.getId();
        if (bookingOutboxRepository.findByReferenceId(referenceId).isEmpty()) {
            BookingOutboxEvent outboxEvent = BookingOutboxEvent.builder()
                    .bookingId(booking.getId())
                    .workerId(trustedWorkerId)
                    .amount(payoutAmount)
                    .referenceId(referenceId)
                    .status("PENDING")
                    .retryCount(0)
                    .nextRetryTime(LocalDateTime.now())
                    .build();
            bookingOutboxRepository.save(outboxEvent);
        }

        publishEvent(updatedBooking, "BOOKING_COMPLETED");
        return updatedBooking;
    }

    @Transactional
    public Booking updateWorkerBookingStatus(Long bookingId, String workerId, BookingStatus newStatus) {
        BookingWorker assignment = bookingWorkerRepository.findByBookingIdAndWorkerId(bookingId, workerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to booking"));
        
        if (newStatus == BookingStatus.COMPLETED || newStatus == BookingStatus.SERVICE_COMPLETED) {
            return completeBooking(bookingId);
        }

        Booking booking = assignment.getBooking();
        booking.setStatus(newStatus);
        Booking updated = bookingRepository.save(booking);

        try {
            BookingWorkerStatus bwStatus = BookingWorkerStatus.valueOf(newStatus.name().replace("WORKER_", ""));
            assignment.setStatus(bwStatus);
            bookingWorkerRepository.save(assignment);
        } catch (Exception ignored) {}

        return updated;
    }

    @Transactional
    public void assignWorker(Long bookingId, String workerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        Optional<BookingWorker> existingAssignment = bookingWorkerRepository.findByBookingIdAndWorkerId(bookingId, workerId);
        if (existingAssignment.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Worker already assigned to this booking");
        }

        try {
            var workerProfile = restTemplate.getForObject(userServiceUrl + "/api/v1/workers/" + workerId + "/profile", Object.class);
            if (workerProfile == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Worker not found or inactive");
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            // Allow if offline or in tests
        }

        boolean available = workerAvailabilityService.isWorkerAvailable(workerId, booking.getServiceDateTime(), booking.getEstimatedDurationMinutes());
        if (!available) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Worker has a scheduling conflict during this time");
        }

        BookingWorker bookingWorker = BookingWorker.builder()
                .booking(booking)
                .workerId(workerId)
                .status(BookingWorkerStatus.ACCEPTED)
                .assignedAt(LocalDateTime.now())
                .build();
        bookingWorkerRepository.save(bookingWorker);

        booking.setAssignedWorkerCount(booking.getAssignedWorkerCount() + 1);
        booking.setStatus(BookingStatus.WORKER_ASSIGNED);
        bookingRepository.save(booking);
    }

    private void enrichWorkerSummary(Booking booking) {
        List<BookingWorker> workers = bookingWorkerRepository.findByBookingId(booking.getId());
        if (!workers.isEmpty()) {
            booking.setBookingWorkers(workers);
            BookingWorker bw = workers.get(0);
            String workerId = bw.getWorkerId();
            try {
                var profile = restTemplate.getForObject(userServiceUrl + "/api/v1/workers/" + workerId + "/profile", java.util.Map.class);
                if (profile != null) {
                    booking.setAssignedWorkerName((String) profile.get("fullName"));
                    Object ratingObj = profile.get("rating");
                    if (ratingObj instanceof Number) {
                        booking.setAssignedWorkerRating(((Number) ratingObj).doubleValue());
                    }
                    booking.setAssignedWorkerProfileImage((String) profile.get("profileImage"));
                }
            } catch (Exception e) {
                booking.setAssignedWorkerName("Worker " + workerId);
                booking.setAssignedWorkerRating(4.5);
            }
        }
    }

    private CatalogServiceDto fetchCatalogService(Long serviceId) {
        try {
            return restTemplate.getForObject(catalogServiceUrl + "/api/v1/services/" + serviceId, CatalogServiceDto.class);
        } catch (Exception e) {
            return null;
        }
    }

    private void publishEvent(Booking booking, String status) {
        try {
            BookingEvent event = BookingEvent.builder()
                    .bookingId(booking.getId().toString())
                    .customerId(booking.getCustomerId())
                    .serviceType(booking.getServiceName())
                    .location(booking.getLocation())
                    .status(status)
                    .timestamp(LocalDateTime.now())
                    .build();
            eventProducer.sendBookingEvent(event);
        } catch (Exception ignored) {}
    }
}
