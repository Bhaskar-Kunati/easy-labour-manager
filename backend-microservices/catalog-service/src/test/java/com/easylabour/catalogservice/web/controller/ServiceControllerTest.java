package com.easylabour.catalogservice.web.controller;

import com.easylabour.catalogservice.application.service.ServiceService;
import com.easylabour.catalogservice.domain.model.PricingType;
import com.easylabour.catalogservice.domain.model.Service;
import com.easylabour.catalogservice.domain.model.ServiceCategory;
import com.easylabour.catalogservice.domain.model.ServiceStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceService serviceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetServices() throws Exception {
        ServiceCategory category = ServiceCategory.builder().id(1L).name("Home").build();
        Service service = Service.builder()
                .id(1L)
                .category(category)
                .name("Plumbing Repair")
                .description("Fix leaking pipes")
                .basePrice(BigDecimal.valueOf(500))
                .pricingType(PricingType.FIXED)
                .status(ServiceStatus.ACTIVE)
                .build();

        Mockito.when(serviceService.getActiveServices(null)).thenReturn(List.of(service));

        mockMvc.perform(get("/api/v1/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Plumbing Repair"));
    }

    @Test
    void testGetServiceById() throws Exception {
        ServiceCategory category = ServiceCategory.builder().id(1L).name("Home").build();
        Service service = Service.builder()
                .id(1L)
                .category(category)
                .name("Plumbing Repair")
                .description("Fix leaking pipes")
                .basePrice(BigDecimal.valueOf(500))
                .pricingType(PricingType.FIXED)
                .status(ServiceStatus.ACTIVE)
                .build();

        Mockito.when(serviceService.getServiceById(1L)).thenReturn(service);

        mockMvc.perform(get("/api/v1/services/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Plumbing Repair"));
    }

    @Test
    void testSearchServices() throws Exception {
        ServiceCategory category = ServiceCategory.builder().id(1L).name("Home").build();
        Service service = Service.builder()
                .id(1L)
                .category(category)
                .name("Plumbing Repair")
                .description("Fix leaking pipes")
                .basePrice(BigDecimal.valueOf(500))
                .pricingType(PricingType.FIXED)
                .status(ServiceStatus.ACTIVE)
                .build();

        Mockito.when(serviceService.searchServices("Plumbing")).thenReturn(List.of(service));

        mockMvc.perform(get("/api/v1/services/search").param("query", "Plumbing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Plumbing Repair"));
    }
}
