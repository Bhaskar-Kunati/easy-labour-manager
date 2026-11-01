package com.easylabour.reviewservice.domain.repository;

import com.easylabour.reviewservice.domain.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByWorkerId(String workerId);
    List<Review> findByServiceId(String serviceId);
    boolean existsByBookingIdAndCustomerId(Long bookingId, String customerId);
}
