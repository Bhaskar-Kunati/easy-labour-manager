package com.easylabour.userservice.application.service;

import com.easylabour.userservice.domain.model.CustomerProfile;
import com.easylabour.userservice.domain.repository.CustomerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerProfileRepository customerProfileRepository;

    @Transactional(readOnly = true)
    public CustomerProfile getCustomerProfile(String userId) {
        return customerProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    @Transactional
    public CustomerProfile updateCustomerProfile(String userId, CustomerProfile profileUpdate) {
        CustomerProfile profile = customerProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        profile.setFullName(profileUpdate.getFullName());
        profile.setProfileImage(profileUpdate.getProfileImage());
        profile.setEmail(profileUpdate.getEmail());
        profile.setGender(profileUpdate.getGender());
        
        return customerProfileRepository.save(profile);
    }
}
