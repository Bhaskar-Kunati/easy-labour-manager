package com.easylabour.reviewservice.web.controller;

import com.easylabour.reviewservice.application.service.ReviewService;
import com.easylabour.reviewservice.domain.model.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        String authenticatedCustomerId = SecurityContextHolder.getContext().getAuthentication().getName();
        Review createdReview = reviewService.createReview(review, authenticatedCustomerId);
        return ResponseEntity.ok(createdReview);
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<Review>> getReviewsByWorker(@PathVariable String workerId) {
        return ResponseEntity.ok(reviewService.getReviewsByWorker(workerId));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<Review>> getReviewsByService(@PathVariable String serviceId) {
        return ResponseEntity.ok(reviewService.getReviewsByService(serviceId));
    }

    @GetMapping("/worker/{workerId}/rating")
    public ResponseEntity<Map<String, Object>> getAverageRatingForWorker(@PathVariable String workerId) {
        Double avgRating = reviewService.getAverageRatingForWorker(workerId);
        return ResponseEntity.ok(Map.of("workerId", workerId, "averageRating", avgRating));
    }

    @GetMapping("/service/{serviceId}/rating")
    public ResponseEntity<Map<String, Object>> getAverageRatingForService(@PathVariable String serviceId) {
        Double avgRating = reviewService.getAverageRatingForService(serviceId);
        return ResponseEntity.ok(Map.of("serviceId", serviceId, "averageRating", avgRating));
    }
}
