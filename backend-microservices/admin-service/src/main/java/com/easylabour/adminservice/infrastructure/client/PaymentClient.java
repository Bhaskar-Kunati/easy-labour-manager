package com.easylabour.adminservice.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentClient {
    private final RestTemplate restTemplate;

    @Value("${services.payment.url:http://localhost:8085}")
    private String paymentServiceUrl;

    @Value("${wallet.internal.secret-key:internal-secret-key}")
    private String internalSecretKey;

    public Map<String, Object> getAnalytics() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Service-Key", internalSecretKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    paymentServiceUrl + "/api/internal/payments/analytics",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch payment analytics from payment-service: " + e.getMessage(), e);
        }
    }
}
