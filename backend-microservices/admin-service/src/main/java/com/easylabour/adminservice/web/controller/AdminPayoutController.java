package com.easylabour.adminservice.web.controller;

import com.easylabour.adminservice.infrastructure.client.WalletClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/payouts")
@RequiredArgsConstructor
public class AdminPayoutController {

    private final WalletClient walletClient;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllPayouts() {
        return ResponseEntity.ok(walletClient.getPayouts());
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> approvePayout(@PathVariable Long id) {
        return ResponseEntity.ok(walletClient.approvePayout(id));
    }
}
