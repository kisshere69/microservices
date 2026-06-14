package com.example.ticket.application;

import com.example.ticket.domain.Ticket;
import com.example.ticket.domain.TicketStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TicketServiceTest {

    private final TicketService ticketService = new TicketService();

    @Test
    void createsTicketWithNewStatus() {
        Ticket ticket = ticketService.createTicket("Payment API is returning 500", "customer-1");

        assertThat(ticket.getId()).isNotBlank();
        assertThat(ticket.getTitle()).isEqualTo("Payment API is returning 500");
        assertThat(ticket.getCustomerId()).isEqualTo("customer-1");
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.NEW);
    }

    @Test
    void movesTicketThroughAllowedStatusTransition() {
        Ticket ticket = ticketService.createTicket("Payment API is returning 500", "customer-1");

        Ticket updatedTicket = ticketService.changeStatus(ticket.getId(), TicketStatus.OPEN);

        assertThat(updatedTicket.getStatus()).isEqualTo(TicketStatus.OPEN);
    }

    @Test
    void rejectsInvalidStatusTransition() {
        Ticket ticket = ticketService.createTicket("Payment API is returning 500", "customer-1");

        assertThatThrownBy(() -> ticketService.changeStatus(ticket.getId(), TicketStatus.RESOLVED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot move ticket");
    }
}
