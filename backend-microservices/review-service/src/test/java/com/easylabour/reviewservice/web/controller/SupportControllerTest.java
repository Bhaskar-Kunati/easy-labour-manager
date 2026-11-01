package com.easylabour.reviewservice.web.controller;

import com.easylabour.reviewservice.application.service.SupportService;
import com.easylabour.reviewservice.domain.model.SupportTicket;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SupportController.class)
class SupportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SupportService supportService;

    @Test
    void testCreateTicketSuccess() throws Exception {
        SupportTicket ticket = SupportTicket.builder()
                .id(1L)
                .userId("cust-1")
                .bookingId(100L)
                .subject("Issue with booking")
                .description("Worker arrived late")
                .status("OPEN")
                .priority("MEDIUM")
                .createdAt(LocalDateTime.now())
                .build();

        when(supportService.createTicket(any(SupportTicket.class))).thenReturn(ticket);

        mockMvc.perform(post("/api/v1/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticket)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.subject").value("Issue with booking"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void testGetTicketsByUser() throws Exception {
        SupportTicket ticket = SupportTicket.builder()
                .id(1L)
                .userId("cust-1")
                .bookingId(100L)
                .subject("Issue with booking")
                .description("Worker arrived late")
                .status("OPEN")
                .priority("MEDIUM")
                .createdAt(LocalDateTime.now())
                .build();

        when(supportService.getTicketsByUser("cust-1")).thenReturn(List.of(ticket));

        mockMvc.perform(get("/api/v1/support/tickets?userId=cust-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("cust-1"))
                .andExpect(jsonPath("$[0].subject").value("Issue with booking"));
    }
}
