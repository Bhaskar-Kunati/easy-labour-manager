package com.easylabour.authservice.web.controller;

import com.easylabour.authservice.domain.model.User;
import com.easylabour.authservice.domain.repository.UserRepository;
import com.easylabour.authservice.domain.service.OtpService;
import com.easylabour.authservice.domain.service.OtpProvider;
import com.easylabour.authservice.dto.request.OtpRequest;
import com.easylabour.authservice.dto.request.OtpVerificationRequest;
import com.easylabour.authservice.security.JwtService;
import com.easylabour.common.enums.UserRole;
import com.easylabour.common.enums.UserStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpProvider otpProvider;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final OtpService otpService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest otpRequest) {
        if (otpRequest == null || otpRequest.getMobileNumber() == null || otpRequest.getMobileNumber().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Mobile number is required"));
        }
        String mobileNumber = otpRequest.getMobileNumber();
        if (!mobileNumber.matches("^\\+?[0-9]{10,15}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid mobile number format"));
        }

        try {
            otpService.sendOtp(mobileNumber);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP sent successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid request or mobile number"));
        } catch (org.springframework.dao.DataAccessException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message", "Database service unavailable"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message", "Service temporarily unavailable"));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        if (request == null || request.getMobileNumber() == null || request.getOtp() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Mobile number and OTP are required"));
        }
        String mobileNumber = request.getMobileNumber();
        String otp = request.getOtp();

        if (mobileNumber.trim().isEmpty() || !mobileNumber.matches("^\\+?[0-9]{10,15}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid mobile number format"));
        }
        if (otp.trim().isEmpty() || !otp.matches("^[0-9]{6}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid OTP format"));
        }

        try {
            otpService.verifyOtp(mobileNumber, otp);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP verified successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (org.springframework.dao.DataAccessException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message", "Database service unavailable"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message", "Service temporarily unavailable"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registerRequest) {
        if (registerRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request body is missing");
        }

        String mobileNumber = registerRequest.get("mobileNumber");
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            mobileNumber = registerRequest.get("phoneNumber"); // backward compatibility
        }
        String password = registerRequest.get("password");

        if (mobileNumber == null || mobileNumber.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mobile number and password are required");
        }

        // Basic syntactic mobile validation: optional leading '+' followed by 10 to 15 digits
        if (!mobileNumber.matches("^\\+?[0-9]{10,15}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid mobile number format");
        }

        try {
            if (userRepository.findByMobileNumber(mobileNumber).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Mobile number already exists");
            }

            User user = User.builder()
                    .mobileNumber(mobileNumber)
                    .passwordHash(passwordEncoder.encode(password))
                    .role(UserRole.CUSTOMER)
                    .status(UserStatus.ACTIVE)
                    .isVerified(false)
                    .build();

            userRepository.save(user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (org.springframework.dao.DataAccessException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Database service unavailable");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        if (loginRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request body is missing");
        }

        String mobileNumber = loginRequest.get("mobileNumber");
        String password = loginRequest.get("password");

        if (mobileNumber == null || mobileNumber.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mobile number and password are required");
        }

        // Basic syntactic mobile validation: optional leading '+' followed by 10 to 15 digits
        if (!mobileNumber.matches("^\\+?[0-9]{10,15}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid mobile number format");
        }

        try {
            return userRepository.findByMobileNumber(mobileNumber)
                    .map(user -> {
                        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
                        }
                        if (user.getStatus() != UserStatus.ACTIVE) {
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
                        }

                        UserDetails userDetails = userDetailsService.loadUserByUsername(mobileNumber);
                        String token = jwtService.generateToken(userDetails);
                        Map<String, Object> response = new HashMap<>();
                        response.put("token", token);
                        response.put("user", user);
                        return ResponseEntity.ok(response);
                    })
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service temporarily unavailable");
        }
    }
}
