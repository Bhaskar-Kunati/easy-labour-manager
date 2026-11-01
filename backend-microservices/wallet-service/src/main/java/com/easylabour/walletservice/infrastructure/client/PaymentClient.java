package com.easylabour.walletservice.infrastructure.client;

import com.easylabour.walletservice.web.dto.PaymentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PaymentClient {
    private final RestTemplate restTemplate;

    @Value("${payment.service.url:http://localhost:8085}")
    private String paymentServiceUrl;

    public PaymentResponseDto getPayment(Long paymentId) {
        return restTemplate.getForObject(paymentServiceUrl + "/api/v1/payments/" + paymentId, PaymentResponseDto.class);
    }
}
