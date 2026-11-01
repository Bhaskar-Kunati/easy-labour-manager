package com.easylabour.walletservice.web.controller;

import com.easylabour.walletservice.application.service.WalletService;
import com.easylabour.walletservice.infrastructure.security.SecurityConfig;
import com.easylabour.walletservice.web.dto.WalletDebitRequest;
import com.easylabour.walletservice.web.dto.WalletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({WalletInternalController.class, WalletController.class, AdminWalletController.class})
@Import(SecurityConfig.class)
@TestPropertySource(properties = "wallet.internal.secret-key=test-secret-key")
class WalletSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Internal Service API Tests ---

    @Test
    void testDebitWithoutInternalKeyRejected() throws Exception {
        WalletDebitRequest request = new WalletDebitRequest();
        request.setUserId("user1");
        request.setAmount(new BigDecimal("10.00"));
        request.setReferenceId("ref-1");

        mockMvc.perform(post("/api/wallet/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDebitWithInvalidInternalKeyRejected() throws Exception {
        WalletDebitRequest request = new WalletDebitRequest();
        request.setUserId("user1");
        request.setAmount(new BigDecimal("10.00"));
        request.setReferenceId("ref-1");

        mockMvc.perform(post("/api/wallet/debit")
                        .header("X-Internal-Service-Key", "wrong-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDebitWithValidInternalKeySuccess() throws Exception {
        WalletDebitRequest request = new WalletDebitRequest();
        request.setUserId("user1");
        request.setAmount(new BigDecimal("10.00"));
        request.setReferenceId("ref-1");

        WalletResponse response = WalletResponse.builder()
                .userId("user1")
                .balance(new BigDecimal("90.00"))
                .currency("INR")
                .status("ACTIVE")
                .build();

        when(walletService.debitWallet(eq("user1"), any(), eq("ref-1"), any())).thenReturn(response);

        mockMvc.perform(post("/api/wallet/debit")
                        .header("X-Internal-Service-Key", "test-secret-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1")
    void testDebitWithCustomerJwtButNoInternalKeyRejected() throws Exception {
        WalletDebitRequest request = new WalletDebitRequest();
        request.setUserId("user1");
        request.setAmount(new BigDecimal("10.00"));
        request.setReferenceId("ref-1");

        // Even with a valid user session, the internal key is required for /debit
        mockMvc.perform(post("/api/wallet/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // --- Customer Wallet API Tests ---

    @Test
    void testGetBalanceUnauthenticatedRejected() throws Exception {
        mockMvc.perform(get("/api/wallet/balance"))
                .andExpect(status().isForbidden()); // Spring Security default for unauthorized if no entry point configured
    }

    @Test
    @WithMockUser(username = "user1")
    void testGetBalanceAuthenticatedSuccess() throws Exception {
        WalletResponse response = WalletResponse.builder()
                .userId("user1")
                .balance(new BigDecimal("100.00"))
                .currency("INR")
                .status("ACTIVE")
                .build();
        when(walletService.getWalletBalance("user1")).thenReturn(response);

        mockMvc.perform(get("/api/wallet/balance"))
                .andExpect(status().isOk());
    }

    // --- Admin Wallet API Tests ---

    @Test
    @WithMockUser(username = "admin", roles = "USER")
    void testAdminApiAccessDeniedForUser() throws Exception {
        mockMvc.perform(get("/api/admin/wallets"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testAdminApiAccessAllowedForAdmin() throws Exception {
        when(walletService.getAllWalletsForAdmin()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/wallets"))
                .andExpect(status().isOk());
    }
}
