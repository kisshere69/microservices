package com.example.ticket.application;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(String ticketId) {
        super("Ticket not found: " + ticketId);
    }
}
