package com.easylabour.catalogservice.web.controller;

import com.easylabour.catalogservice.application.service.CategoryService;
import com.easylabour.catalogservice.domain.model.ServiceCategory;
import com.easylabour.catalogservice.domain.model.ServiceStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllActiveCategories() throws Exception {
        ServiceCategory category = ServiceCategory.builder()
                .id(1L)
                .name("Home Services")
                .description("Home maintenance and repair")
                .status(ServiceStatus.ACTIVE)
                .build();

        Mockito.when(categoryService.getAllActiveCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Home Services"));
    }

    @Test
    void testGetCategoryById() throws Exception {
        ServiceCategory category = ServiceCategory.builder()
                .id(1L)
                .name("Home Services")
                .description("Home maintenance and repair")
                .status(ServiceStatus.ACTIVE)
                .build();

        Mockito.when(categoryService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Home Services"));
    }
}
