package com.easylabour.authservice.dto.request;

import lombok.Data;

@Data
public class OtpRequest {
    private String mobileNumber;
    private String countryCode;
}
