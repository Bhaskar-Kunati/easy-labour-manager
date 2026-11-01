package com.easylabour.authservice.dto.request;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String mobileNumber;
    private String otp;
}
