package com.easylabour.notificationservice.web.controller;

import com.easylabour.notificationservice.application.service.NotificationService;
import com.easylabour.notificationservice.domain.model.Notification;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    @WithMockUser(username = "+1234567890")
    void testGetNotificationsSuccess() throws Exception {
        Notification notification = Notification.builder()
                .id(1L)
                .userId("+1234567890")
                .title("Booking Confirmation")
                .message("Your booking has been created.")
                .notificationType("BOOKING_CREATED")
                .isRead(false)
                .build();

        Mockito.when(notificationService.getNotifications("+1234567890")).thenReturn(List.of(notification));

        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Booking Confirmation"))
                .andExpect(jsonPath("$[0].isRead").value(false));
    }

    @Test
    @WithMockUser(username = "+1234567890")
    void testMarkAsReadSuccess() throws Exception {
        Mockito.doNothing().when(notificationService).markAsRead(eq(1L), eq("+1234567890"));

        mockMvc.perform(put("/api/v1/notifications/1/read"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "+1234567890")
    void testMarkAsReadForbidden() throws Exception {
        Mockito.doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"))
                .when(notificationService).markAsRead(eq(2L), eq("+1234567890"));

        mockMvc.perform(put("/api/v1/notifications/2/read"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetNotificationsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isUnauthorized());
    }
}
