package com.easylabour.authservice.web.controller;

import com.easylabour.authservice.domain.model.User;
import com.easylabour.authservice.domain.repository.UserRepository;
import com.easylabour.authservice.domain.service.OtpProvider;
import com.easylabour.authservice.domain.service.OtpService;
import com.easylabour.authservice.security.JwtService;
import com.easylabour.common.enums.UserRole;
import com.easylabour.common.enums.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private OtpService otpService;

    @MockBean
    private OtpProvider otpProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSendOtpSuccess() throws Exception {
        User user = User.builder().mobileNumber("+1234567890").build();
        Mockito.when(userRepository.findByMobileNumber("+1234567890")).thenReturn(Optional.of(user));

        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "+1234567890");

        mockMvc.perform(post("/api/v1/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent successfully"));
    }

    @Test
    void testSendOtpUnknownMobile() throws Exception {
        Mockito.when(userRepository.findByMobileNumber("+9999999999")).thenReturn(Optional.empty());

        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "+9999999999");

        mockMvc.perform(post("/api/v1/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendOtpMalformedMobile() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "abc");

        mockMvc.perform(post("/api/v1/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendOtpServiceUnavailableOnProviderFailure() throws Exception {
        User user = User.builder().mobileNumber("+1234567890").build();
        Mockito.when(userRepository.findByMobileNumber("+1234567890")).thenReturn(Optional.of(user));
        Mockito.doThrow(new RuntimeException("SMS Gateway Error")).when(otpProvider).sendOtp(eq("+1234567890"), anyString());

        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "+1234567890");

        mockMvc.perform(post("/api/v1/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable());

        // Verify that provider failure does not leave an active session (verifying with any code should fail with HTTP 400 because session was removed)
        Map<String, String> verifyRequest = new HashMap<>();
        verifyRequest.put("mobileNumber", "+1234567890");
        verifyRequest.put("otp", "123456");

        mockMvc.perform(post("/api/v1/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVerifyOtpSuccessSetsIsVerifiedTrue() throws Exception {
        User user = User.builder().mobileNumber("+1234567890").isVerified(false).build();
        Mockito.when(userRepository.findByMobileNumber("+1234567890")).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            assertTrue(saved.isVerified());
            return saved;
        });

        ArgumentCaptor<String> otpCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.doNothing().when(otpProvider).sendOtp(eq("+1234567890"), otpCaptor.capture());

        Map<String, String> sendRequest = new HashMap<>();
        sendRequest.put("mobileNumber", "+1234567890");
        mockMvc.perform(post("/api/v1/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendRequest)))
                .andExpect(status().isOk());

        String generatedOtp = otpCaptor.getValue();

        Map<String, String> verifyRequest = new HashMap<>();
        verifyRequest.put("mobileNumber", "+1234567890");
        verifyRequest.put("otp", generatedOtp);

        mockMvc.perform(post("/api/v1/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP verified successfully"));
    }

    @Test
    void testVerifyOtpExpiredReturnsBadRequest() throws Exception {
        User user = User.builder().mobileNumber("+1234567890").isVerified(false).build();
        Mockito.when(userRepository.findByMobileNumber("+1234567890")).thenReturn(Optional.of(user));

        ArgumentCaptor<String> otpCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.doNothing().when(otpProvider).sendOtp(eq("+1234567890"), otpCaptor.capture());

        Map<String, String> sendRequest = new HashMap<>();
        sendRequest.put("mobileNumber", "+1234567890");
        mockMvc.perform(post("/api/v1/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendRequest)))
                .andExpect(status().isOk());

        String generatedOtp = otpCaptor.getValue();

        otpService.expireSessionForTest("+1234567890");

        Map<String, String> verifyRequest = new HashMap<>();
        verifyRequest.put("mobileNumber", "+1234567890");
        verifyRequest.put("otp", generatedOtp);

        mockMvc.perform(post("/api/v1/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testThreeFailedAttemptsInvalidatesOtpAndFourthAttemptFails() throws Exception {
        User user = User.builder().mobileNumber("+1234567890").isVerified(false).build();
        Mockito.when(userRepository.findByMobileNumber("+1234567890")).thenReturn(Optional.of(user));

        ArgumentCaptor<String> otpCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.doNothing().when(otpProvider).sendOtp(eq("+1234567890"), otpCaptor.capture());

        Map<String, String> sendRequest = new HashMap<>();
        sendRequest.put("mobileNumber", "+1234567890");
        mockMvc.perform(post("/api/v1/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendRequest)))
                .andExpect(status().isOk());

        String correctOtp = otpCaptor.getValue();

        Map<String, String> wrongRequest = new HashMap<>();
        wrongRequest.put("mobileNumber", "+1234567890");
        wrongRequest.put("otp", "000000");

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/v1/auth/verify-otp")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(wrongRequest)))
                    .andExpect(status().isBadRequest());
        }

        Map<String, String> correctRequest = new HashMap<>();
        correctRequest.put("mobileNumber", "+1234567890");
        correctRequest.put("otp", correctOtp);

        mockMvc.perform(post("/api/v1/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testResendSupersedesPreviousOtp() throws Exception {
        User user = User.builder().mobileNumber("+1234567890").isVerified(false).build();
        Mockito.when(userRepository.findByMobileNumber("+1234567890")).thenReturn(Optional.of(user));

        ArgumentCaptor<String> otpCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.doNothing().when(otpProvider).sendOtp(eq("+1234567890"), otpCaptor.capture());

        Map<String, String> sendRequest = new HashMap<>();
        sendRequest.put("mobileNumber", "+1234567890");

        mockMvc.perform(post("/api/v1/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendRequest)))
                .andExpect(status().isOk());
        String otp1 = otpCaptor.getValue();

        mockMvc.perform(post("/api/v1/auth/send-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendRequest)))
                .andExpect(status().isOk());
        String otp2 = otpCaptor.getValue();

        Map<String, String> verifyRequest1 = new HashMap<>();
        verifyRequest1.put("mobileNumber", "+1234567890");
        verifyRequest1.put("otp", otp1);
        mockMvc.perform(post("/api/v1/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest1)))
                .andExpect(status().isBadRequest());

        Map<String, String> verifyRequest2 = new HashMap<>();
        verifyRequest2.put("mobileNumber", "+1234567890");
        verifyRequest2.put("otp", otp2);
        mockMvc.perform(post("/api/v1/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest2)))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginSuccessActiveCustomer() throws Exception {
        User user = User.builder()
                .id("1")
                .mobileNumber("+1234567890")
                .passwordHash("hashedPass")
                .status(UserStatus.ACTIVE)
                .role(UserRole.CUSTOMER)
                .build();

        Mockito.when(userRepository.findByMobileNumber("+1234567890")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("password123", "hashedPass")).thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername("+1234567890"))
                .thenReturn(org.springframework.security.core.userdetails.User.withUsername("+1234567890").password("").authorities("ROLE_CUSTOMER").build());
        Mockito.when(jwtService.generateToken(Mockito.any(UserDetails.class))).thenReturn("mock-jwt-token");

        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "+1234567890");
        request.put("password", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void testLoginInvalidCredentials() throws Exception {
        User user = User.builder()
                .id("1")
                .mobileNumber("+1234567890")
                .passwordHash("hashedPass")
                .status(UserStatus.ACTIVE)
                .role(UserRole.CUSTOMER)
                .build();

        Mockito.when(userRepository.findByMobileNumber("+1234567890")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("wrongpass", "hashedPass")).thenReturn(false);

        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "+1234567890");
        request.put("password", "wrongpass");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginInvalidMobileFormat() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "invalid-mobile");
        request.put("password", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginMissingFields() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "+1234567890");
        // missing password

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginSuspendedCustomer() throws Exception {
        User user = User.builder()
                .id("1")
                .mobileNumber("+1234567890")
                .passwordHash("hashedPass")
                .status(UserStatus.SUSPENDED)
                .role(UserRole.CUSTOMER)
                .build();

        Mockito.when(userRepository.findByMobileNumber("+1234567890")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("password123", "hashedPass")).thenReturn(true);

        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "+1234567890");
        request.put("password", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginBlockedCustomer() throws Exception {
        User user = User.builder()
                .id("1")
                .mobileNumber("+1234567890")
                .passwordHash("hashedPass")
                .status(UserStatus.BLOCKED)
                .role(UserRole.CUSTOMER)
                .build();

        Mockito.when(userRepository.findByMobileNumber("+1234567890")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("password123", "hashedPass")).thenReturn(true);

        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "+1234567890");
        request.put("password", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginServiceUnavailableOnException() throws Exception {
        Mockito.when(userRepository.findByMobileNumber(anyString())).thenThrow(new RuntimeException("Database error"));

        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "+1234567890");
        request.put("password", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void testRegisterSuccess() throws Exception {
        Mockito.when(userRepository.findByMobileNumber("+1234567890")).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode("SecurePassword123")).thenReturn("hashedPassword123");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "+1234567890");
        request.put("password", "SecurePassword123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void testRegisterDuplicateMobile() throws Exception {
        User existing = User.builder().mobileNumber("+1234567890").build();
        Mockito.when(userRepository.findByMobileNumber("+1234567890")).thenReturn(Optional.of(existing));

        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "+1234567890");
        request.put("password", "SecurePassword123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void testRegisterInvalidMobileFormat() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "not-a-number");
        request.put("password", "SecurePassword123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterMissingFields() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "+1234567890");
        // missing password

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterDatabaseUnavailable() throws Exception {
        Mockito.when(userRepository.findByMobileNumber(anyString())).thenThrow(new org.springframework.dao.DataAccessResourceFailureException("DB down"));

        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", "+1234567890");
        request.put("password", "SecurePassword123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable());
    }
}
