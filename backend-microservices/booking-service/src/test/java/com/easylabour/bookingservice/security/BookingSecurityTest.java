package com.easylabour.bookingservice.security;

import com.easylabour.bookingservice.application.service.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void testUnauthenticatedWorkerApiAccessRejected() throws Exception {
        mockMvc.perform(get("/api/v1/workers/bookings"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testCustomerAccessToWorkerApiRejected() throws Exception {
        mockMvc.perform(get("/api/v1/workers/bookings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "worker-2", roles = {"WORKER"})
    void testWorkerAccessOtherWorkerBookingDetailsRejected() throws Exception {
        Mockito.when(bookingService.getWorkerBookingDetails(1L, "worker-2"))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));

        mockMvc.perform(get("/api/v1/workers/bookings/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"WORKER"})
    void testWorkerAccessAdminAssignmentRejected() throws Exception {
        mockMvc.perform(post("/api/v1/admin/bookings/1/assign-worker")
                        .param("workerId", "worker-1"))
                .andExpect(status().isForbidden());
    }
}
