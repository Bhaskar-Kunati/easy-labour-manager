package com.easylabour.adminservice.web.controller;

import com.easylabour.adminservice.application.service.AdminAnalyticsService;
import com.easylabour.adminservice.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAnalyticsController.class)
@Import(SecurityConfig.class)
class AdminAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminAnalyticsService adminAnalyticsService;

    @Test
    void testGetOverviewUnauthenticatedRejected() throws Exception {
        mockMvc.perform(get("/api/v1/admin/analytics/overview"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetOverviewAsAdminSuccess() throws Exception {
        when(adminAnalyticsService.getOverview()).thenReturn(Map.of("gmv", BigDecimal.TEN));

        mockMvc.perform(get("/api/v1/admin/analytics/overview"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    void testGetOverviewAsCustomerForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/analytics/overview"))
                .andExpect(status().isForbidden());
    }
}
