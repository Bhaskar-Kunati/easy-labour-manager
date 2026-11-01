package com.easylabour.userservice.web.controller;

import com.easylabour.userservice.application.service.CustomerService;
import com.easylabour.userservice.domain.model.CustomerProfile;
import com.easylabour.userservice.infrastructure.persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final JpaUserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<CustomerProfile> getMe() {
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String userId = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("User not found"))
            .getId();
        return ResponseEntity.ok(customerService.getCustomerProfile(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<CustomerProfile> updateMe(@RequestBody CustomerProfile profile) {
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String userId = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("User not found"))
            .getId();
        return ResponseEntity.ok(customerService.updateCustomerProfile(userId, profile));
    }
}
