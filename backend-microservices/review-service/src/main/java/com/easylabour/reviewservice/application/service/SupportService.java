package com.easylabour.reviewservice.application.service;

import com.easylabour.reviewservice.domain.model.SupportTicket;
import com.easylabour.reviewservice.domain.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportService {
    private final SupportTicketRepository supportTicketRepository;

    @Transactional
    public SupportTicket createTicket(SupportTicket ticket) {
        return supportTicketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<SupportTicket> getTicketsByUser(String userId) {
        return supportTicketRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public SupportTicket getTicketById(Long id) {
        return supportTicketRepository.findById(id).orElseThrow();
    }
}
