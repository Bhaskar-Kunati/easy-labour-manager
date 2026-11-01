package com.easylabour.authservice.domain.service;

public interface OtpProvider {
    void sendOtp(String mobileNumber, String otp);
}
