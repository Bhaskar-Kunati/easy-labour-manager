package com.easylabour.authservice.domain.service;

import com.easylabour.authservice.domain.model.User;
import com.easylabour.authservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository;
    private final OtpProvider otpProvider;
    private final SecureRandom secureRandom = new SecureRandom();

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long EXPIRATION_MINUTES = 5;

    public static class OtpSession {
        private final String otp;
        private final Instant expiresAt;
        private int attempts;

        public OtpSession(String otp, Instant expiresAt) {
            this.otp = otp;
            this.expiresAt = expiresAt;
            this.attempts = 0;
        }

        public Instant getExpiresAt() {
            return expiresAt;
        }

        public int getAttempts() {
            return attempts;
        }

        public boolean validate(String submittedOtp) {
            return this.otp.equals(submittedOtp);
        }

        public void incrementAttempts() {
            this.attempts++;
        }
    }

    private final Map<String, OtpSession> otpStore = new ConcurrentHashMap<>();

    public void sendOtp(String mobileNumber) {
        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String otp = String.format("%06d", secureRandom.nextInt(1000000));
        Instant expiresAt = Instant.now().plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES);

        OtpSession session = new OtpSession(otp, expiresAt);
        otpStore.put(mobileNumber, session);

        try {
            otpProvider.sendOtp(mobileNumber, otp);
        } catch (Exception e) {
            otpStore.remove(mobileNumber);
            throw e;
        }
    }

    public void verifyOtp(String mobileNumber, String otpCode) {
        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OtpSession session = otpStore.get(mobileNumber);
        if (session == null) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        if (Instant.now().isAfter(session.getExpiresAt())) {
            otpStore.remove(mobileNumber);
            throw new IllegalArgumentException("OTP has expired");
        }

        if (session.getAttempts() >= MAX_FAILED_ATTEMPTS) {
            otpStore.remove(mobileNumber);
            throw new IllegalArgumentException("Too many failed verification attempts");
        }

        if (!session.validate(otpCode)) {
            session.incrementAttempts();
            if (session.getAttempts() >= MAX_FAILED_ATTEMPTS) {
                otpStore.remove(mobileNumber);
            }
            throw new IllegalArgumentException("Invalid OTP");
        }

        otpStore.remove(mobileNumber);
        user.setVerified(true);
        userRepository.save(user);
    }

    // Public helper for tests to simulate expiration without arbitrary OTP injection
    public void expireSessionForTest(String mobileNumber) {
        OtpSession session = otpStore.get(mobileNumber);
        if (session != null) {
            otpStore.put(mobileNumber, new OtpSession(session.otp, Instant.now().minusSeconds(10)));
        }
    }
}
