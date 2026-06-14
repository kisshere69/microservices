package com.example.ticket.api.dto;

import com.example.ticket.domain.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTicketStatusRequest(
        @NotNull TicketStatus status
) {
}
