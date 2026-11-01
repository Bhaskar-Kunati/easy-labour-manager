package com.easylabour.reviewservice.application.service;

import com.easylabour.common.event.ReviewCreatedEvent;
import com.easylabour.reviewservice.domain.model.Review;
import com.easylabour.reviewservice.domain.repository.ReviewRepository;
import com.easylabour.reviewservice.infrastructure.kafka.ReviewEventProducer;
import com.easylabour.reviewservice.infrastructure.kafka.BookingEventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingEventConsumer bookingEventConsumer;
    private final ReviewEventProducer reviewEventProducer;

    @Transactional
    public Review createReview(Review review, String authenticatedCustomerId) {
        if (review.getBookingId() == null) {
            throw new IllegalArgumentException("Booking ID is required");
        }

        // Enforce customer ownership from security context
        if (authenticatedCustomerId == null || authenticatedCustomerId.isBlank()) {
            throw new AccessDeniedException("Authenticated customer identity is required");
        }
        review.setCustomerId(authenticatedCustomerId);

        // Verify booking exists and is COMPLETED
        if (!bookingEventConsumer.isBookingCompleted(review.getBookingId())) {
            throw new IllegalStateException("Review can only be created for COMPLETED bookings");
        }

        // Verify booking ownership
        if (!bookingEventConsumer.verifyCustomerOwnership(review.getBookingId(), authenticatedCustomerId)) {
            throw new AccessDeniedException("Booking does not belong to the authenticated customer");
        }

        // Verify rating is between 1 and 5
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Prevent duplicate reviews per booking
        if (reviewRepository.existsByBookingIdAndCustomerId(review.getBookingId(), authenticatedCustomerId)) {
            throw new IllegalStateException("Review already exists for this booking");
        }

        Review savedReview = reviewRepository.save(review);

        try {
            ReviewCreatedEvent event = ReviewCreatedEvent.builder()
                    .reviewId(savedReview.getId())
                    .bookingId(String.valueOf(savedReview.getBookingId()))
                    .raterId(savedReview.getCustomerId())
                    .rateeId(savedReview.getWorkerId())
                    .rating(savedReview.getRating())
                    .build();
            reviewEventProducer.sendReviewCreatedEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish ReviewCreatedEvent", e);
        }

        return savedReview;
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByWorker(String workerId) {
        return reviewRepository.findByWorkerId(workerId);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByService(String serviceId) {
        return reviewRepository.findByServiceId(serviceId);
    }

    @Transactional(readOnly = true)
    public Double getAverageRatingForWorker(String workerId) {
        List<Review> reviews = reviewRepository.findByWorkerId(workerId);
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        double sum = reviews.stream().mapToInt(Review::getRating).sum();
        return sum / reviews.size();
    }

    @Transactional(readOnly = true)
    public Double getAverageRatingForService(String serviceId) {
        List<Review> reviews = reviewRepository.findByServiceId(serviceId);
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        double sum = reviews.stream().mapToInt(Review::getRating).sum();
        return sum / reviews.size();
    }
}
