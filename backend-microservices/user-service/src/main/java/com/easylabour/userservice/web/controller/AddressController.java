package com.easylabour.userservice.web.controller;

import com.easylabour.userservice.application.service.AddressService;
import com.easylabour.userservice.domain.model.Address;
import com.easylabour.userservice.infrastructure.persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;
    private final JpaUserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Address>> getAddresses() {
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String userId = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("User not found"))
            .getId();
        return ResponseEntity.ok(addressService.getAddresses(userId));
    }

    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String userId = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("User not found"))
            .getId();
        return ResponseEntity.ok(addressService.createAddress(userId, address));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody Address address) {
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String userId = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("User not found"))
            .getId();
        return ResponseEntity.ok(addressService.updateAddress(userId, id, address));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String userId = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("User not found"))
            .getId();
        addressService.deleteAddress(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<Void> setDefaultAddress(@PathVariable Long id) {
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String userId = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("User not found"))
            .getId();
        addressService.setDefaultAddress(userId, id);
        return ResponseEntity.noContent().build();
    }
}
