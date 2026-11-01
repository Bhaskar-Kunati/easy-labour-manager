package com.easylabour.reviewservice.web.controller;

import com.easylabour.common.event.BookingEvent;
import com.easylabour.reviewservice.application.service.ReviewService;
import com.easylabour.reviewservice.domain.model.Review;
import com.easylabour.reviewservice.infrastructure.kafka.BookingEventConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private BookingEventConsumer bookingEventConsumer;

    @Test
    @WithMockUser(username = "cust-1")
    void testCreateReviewSuccess() throws Exception {
        Review review = Review.builder()
                .id(1L)
                .bookingId(100L)
                .customerId("cust-1")
                .workerId("worker-1")
                .serviceId("service-1")
                .rating(5)
                .comment("Great service!")
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewService.createReview(any(Review.class), eq("cust-1"))).thenReturn(review);

        mockMvc.perform(post("/api/v1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Great service!"));
    }

    @Test
    @WithMockUser(username = "cust-1")
    void testGetReviewsByWorker() throws Exception {
        Review review = Review.builder()
                .id(1L)
                .bookingId(100L)
                .customerId("cust-1")
                .workerId("worker-1")
                .rating(4)
                .comment("Good")
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewService.getReviewsByWorker("worker-1")).thenReturn(List.of(review));

        mockMvc.perform(get("/api/v1/reviews/worker/worker-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].workerId").value("worker-1"))
                .andExpect(jsonPath("$[0].rating").value(4));
    }

    @Test
    @WithMockUser(username = "cust-1")
    void testGetReviewsByService() throws Exception {
        Review review = Review.builder()
                .id(1L)
                .bookingId(100L)
                .customerId("cust-1")
                .workerId("worker-1")
                .serviceId("service-1")
                .rating(5)
                .comment("Excellent")
                .createdAt(LocalDateTime.now())
                .build();

        when(reviewService.getReviewsByService("service-1")).thenReturn(List.of(review));

        mockMvc.perform(get("/api/v1/reviews/service/service-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceId").value("service-1"))
                .andExpect(jsonPath("$[0].rating").value(5));
    }

    @Test
    @WithMockUser(username = "cust-1")
    void testGetAverageRatingForWorker() throws Exception {
        when(reviewService.getAverageRatingForWorker("worker-1")).thenReturn(4.5);

        mockMvc.perform(get("/api/v1/reviews/worker/worker-1/rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workerId").value("worker-1"))
                .andExpect(jsonPath("$.averageRating").value(4.5));
    }

    @Test
    @WithMockUser(username = "cust-1")
    void testGetAverageRatingForService() throws Exception {
        when(reviewService.getAverageRatingForService("service-1")).thenReturn(4.0);

        mockMvc.perform(get("/api/v1/reviews/service/service-1/rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceId").value("service-1"))
                .andExpect(jsonPath("$.averageRating").value(4.0));
    }
}
