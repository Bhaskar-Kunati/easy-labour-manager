package com.easylabour.walletservice.web.controller;

import com.easylabour.walletservice.application.service.PayoutService;
import com.easylabour.walletservice.web.dto.PayoutRequestDto;
import com.easylabour.walletservice.web.dto.PayoutResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutService payoutService;

    private String getAuthenticatedUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    public ResponseEntity<PayoutResponseDto> requestPayout(@Valid @RequestBody PayoutRequestDto requestDto) {
        String userId = getAuthenticatedUserId();
        PayoutResponseDto response = payoutService.requestPayout(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PayoutResponseDto>> getWorkerPayouts() {
        String userId = getAuthenticatedUserId();
        List<PayoutResponseDto> response = payoutService.getWorkerPayouts(userId);
        return ResponseEntity.ok(response);
    }
}
