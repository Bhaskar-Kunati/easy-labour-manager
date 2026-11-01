package com.easylabour.bookingservice.web.controller;

import com.easylabour.bookingservice.application.service.CouponService;
import com.easylabour.bookingservice.domain.model.Coupon;
import com.easylabour.bookingservice.domain.model.CouponStatus;
import com.easylabour.bookingservice.domain.model.DiscountType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateCouponSuccess() throws Exception {
        Coupon coupon = Coupon.builder()
                .id(1L)
                .code("SAVE10")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(BigDecimal.valueOf(10))
                .status(CouponStatus.ACTIVE)
                .build();

        Mockito.when(couponService.createCoupon(any(Coupon.class))).thenReturn(coupon);

        mockMvc.perform(post("/api/v1/admin/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coupon))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("SAVE10"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAllCouponsSuccess() throws Exception {
        Coupon coupon = Coupon.builder()
                .id(1L)
                .code("SAVE10")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(BigDecimal.valueOf(10))
                .status(CouponStatus.ACTIVE)
                .build();

        Mockito.when(couponService.getAllCoupons()).thenReturn(List.of(coupon));

        mockMvc.perform(get("/api/v1/admin/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("SAVE10"));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testCreateCouponForbiddenForCustomer() throws Exception {
        Coupon coupon = Coupon.builder()
                .code("SAVE10")
                .build();

        mockMvc.perform(post("/api/v1/admin/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coupon))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateCouponUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/admin/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
