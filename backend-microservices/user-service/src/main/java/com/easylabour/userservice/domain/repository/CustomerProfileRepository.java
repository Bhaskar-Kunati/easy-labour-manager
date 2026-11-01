package com.easylabour.userservice.domain.repository;

import com.easylabour.userservice.domain.model.CustomerProfile;
import com.easylabour.userservice.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
    Optional<CustomerProfile> findByUser(User user);
    Optional<CustomerProfile> findByUserId(String userId);
}
