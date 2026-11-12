package com.easylabour.walletservice.domain.repository;

import com.easylabour.walletservice.domain.model.PayoutRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayoutRequestRepository extends JpaRepository<PayoutRequest, Long> {
    List<PayoutRequest> findByUserId(String userId);
    List<PayoutRequest> findByStatus(String status);
    Optional<PayoutRequest> findByReferenceId(String referenceId);
}
