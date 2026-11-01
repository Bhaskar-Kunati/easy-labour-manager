package com.easylabour.reviewservice.application.service;

import com.easylabour.reviewservice.domain.model.Review;
import com.easylabour.reviewservice.domain.repository.ReviewRepository;
import com.easylabour.reviewservice.infrastructure.kafka.BookingEventConsumer;
import com.easylabour.reviewservice.infrastructure.kafka.ReviewEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingEventConsumer bookingEventConsumer;

    @Mock
    private ReviewEventProducer reviewEventProducer;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReviewSuccess() {
        Review review = Review.builder()
                .bookingId(100L)
                .workerId("worker-1")
                .serviceId("service-1")
                .rating(5)
                .comment("Great!")
                .build();

        when(bookingEventConsumer.isBookingCompleted(100L)).thenReturn(true);
        when(bookingEventConsumer.verifyCustomerOwnership(100L, "cust-1")).thenReturn(true);
        when(reviewRepository.existsByBookingIdAndCustomerId(100L, "cust-1")).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review result = reviewService.createReview(review, "cust-1");

        assertNotNull(result);
        assertEquals("cust-1", result.getCustomerId());
        assertEquals(5, result.getRating());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testCreateReviewNotCompletedThrowsException() {
        Review review = Review.builder()
                .bookingId(100L)
                .workerId("worker-1")
                .rating(5)
                .build();

        when(bookingEventConsumer.isBookingCompleted(100L)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> reviewService.createReview(review, "cust-1"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testCreateReviewOwnershipViolationThrowsException() {
        Review review = Review.builder()
                .bookingId(100L)
                .workerId("worker-1")
                .rating(5)
                .build();

        when(bookingEventConsumer.isBookingCompleted(100L)).thenReturn(true);
        when(bookingEventConsumer.verifyCustomerOwnership(100L, "cust-1")).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> reviewService.createReview(review, "cust-1"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testCreateReviewInvalidRatingThrowsException() {
        Review review = Review.builder()
                .bookingId(100L)
                .workerId("worker-1")
                .rating(6)
                .build();

        when(bookingEventConsumer.isBookingCompleted(100L)).thenReturn(true);
        when(bookingEventConsumer.verifyCustomerOwnership(100L, "cust-1")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(review, "cust-1"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testCreateReviewDuplicateThrowsException() {
        Review review = Review.builder()
                .bookingId(100L)
                .workerId("worker-1")
                .rating(5)
                .build();

        when(bookingEventConsumer.isBookingCompleted(100L)).thenReturn(true);
        when(bookingEventConsumer.verifyCustomerOwnership(100L, "cust-1")).thenReturn(true);
        when(reviewRepository.existsByBookingIdAndCustomerId(100L, "cust-1")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> reviewService.createReview(review, "cust-1"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testAverageRatingCalculationEmpty() {
        when(reviewRepository.findByWorkerId("worker-1")).thenReturn(List.of());
        Double avg = reviewService.getAverageRatingForWorker("worker-1");
        assertEquals(0.0, avg);
    }

    @Test
    void testAverageRatingCalculationSuccess() {
        Review r1 = Review.builder().rating(4).build();
        Review r2 = Review.builder().rating(5).build();
        when(reviewRepository.findByWorkerId("worker-1")).thenReturn(List.of(r1, r2));

        Double avg = reviewService.getAverageRatingForWorker("worker-1");
        assertEquals(4.5, avg);
    }
}
