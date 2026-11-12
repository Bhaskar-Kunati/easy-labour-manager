package com.easylabour.bookingservice.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WalletClient {
    private final RestTemplate restTemplate;

    @Value("${wallet.service.url:http://localhost:8089}")
    private String walletServiceUrl;

    @Value("${wallet.internal.secret-key:internal-secret-key}")
    private String internalSecretKey;

    public void creditWallet(String workerId, BigDecimal amount, String referenceId, String description) {
        String url = walletServiceUrl + "/api/wallet/credit";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Service-Key", internalSecretKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", workerId);
        requestBody.put("amount", amount);
        requestBody.put("referenceId", referenceId);
        requestBody.put("description", description != null ? description : "Booking Earnings");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Wallet credit failed with status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                // Idempotent duplicate: matching response in wallet-service is treated as successful delivery
                return;
            }
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                // Configuration or security failure
                throw new SecurityException("Wallet authentication/authorization failed (401/403): " + e.getResponseBodyAsString(), e);
            }
            throw new RuntimeException("Wallet client error: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Wallet service server error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            throw new RuntimeException("Wallet service connection or transient failure: " + e.getMessage(), e);
        }
    }
}
