package com.easylabour.authservice.infrastructure.otp;

import com.easylabour.authservice.domain.service.OtpProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MockOtpProvider implements OtpProvider {
    private static final Logger logger = LoggerFactory.getLogger(MockOtpProvider.class);

    @Override
    public void sendOtp(String mobileNumber, String otp) {
        logger.info("MOCK OTP dispatched to mobile number: {}", mobileNumber);
    }
}
