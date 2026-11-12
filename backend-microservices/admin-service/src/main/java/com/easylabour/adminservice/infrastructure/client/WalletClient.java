package com.easylabour.adminservice.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WalletClient {
    private final RestTemplate restTemplate;

    @Value("${services.wallet.url:http://localhost:8089}")
    private String walletServiceUrl;

    @Value("${wallet.internal.secret-key:internal-secret-key}")
    private String internalSecretKey;

    public Map<String, Object> getAnalytics() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Service-Key", internalSecretKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    walletServiceUrl + "/api/wallet/internal/analytics",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch wallet analytics from wallet-service: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> getPayouts() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Service-Key", internalSecretKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    walletServiceUrl + "/api/wallet/internal/payouts",
                    HttpMethod.GET,
                    entity,
                    List.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch payouts from wallet-service: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> approvePayout(Long payoutId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Service-Key", internalSecretKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    walletServiceUrl + "/api/wallet/internal/payouts/" + payoutId + "/approve",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to approve payout in wallet-service: " + e.getMessage(), e);
        }
    }
}
