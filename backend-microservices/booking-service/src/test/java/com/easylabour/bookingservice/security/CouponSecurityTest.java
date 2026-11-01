package com.easylabour.bookingservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CouponSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAdminCouponsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/admin/coupons"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testAdminCouponsForbiddenForCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/admin/coupons"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testCreateCouponForbiddenForCustomer() throws Exception {
        mockMvc.perform(post("/api/v1/admin/coupons")
                        .contentType("application/json")
                        .content("{}")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
