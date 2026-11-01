package com.easylabour.reviewservice.web.controller;

import com.easylabour.reviewservice.application.service.SupportService;
import com.easylabour.reviewservice.domain.model.SupportTicket;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/support/tickets")
@RequiredArgsConstructor
public class SupportController {
    private final SupportService supportService;

    @PostMapping
    public ResponseEntity<SupportTicket> createTicket(@RequestBody SupportTicket ticket) {
        return ResponseEntity.ok(supportService.createTicket(ticket));
    }

    @GetMapping
    public ResponseEntity<List<SupportTicket>> getTicketsByUser(@RequestParam String userId) {
        return ResponseEntity.ok(supportService.getTicketsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportTicket> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(supportService.getTicketById(id));
    }
}
