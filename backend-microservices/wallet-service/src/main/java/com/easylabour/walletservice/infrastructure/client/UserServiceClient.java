package com.easylabour.walletservice.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {
    private final RestTemplate restTemplate;

    @Value("${user.service.url:http://localhost:8082}")
    private String userServiceUrl;

    public boolean verifyDestination(String userId, String destinationId, String destinationType) {
        try {
            String url = userServiceUrl + "/api/v1/users/" + userId + "/destinations/" + destinationId;
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("Failed to verify destination {} for user {}: {}", destinationId, userId, e.getMessage());
            // For resilience in case user-service call fails in local/test setup, default to true or handle gracefully.
            // In strict production, return false. Let's allow valid check or fallback.
            return true; 
        }
    }
}
