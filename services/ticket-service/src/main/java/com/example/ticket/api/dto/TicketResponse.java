package com.example.ticket.api.dto;

import com.example.ticket.domain.Ticket;
import com.example.ticket.domain.TicketStatus;

import java.time.Instant;

public record TicketResponse(
        String id,
        String title,
        String customerId,
        TicketStatus status,
        Instant createdAt
) {

    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getCustomerId(),
                ticket.getStatus(),
                ticket.getCreatedAt()
        );
    }
}
