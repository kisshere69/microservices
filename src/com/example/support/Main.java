package com.example.support;

import static java.util.UUID.randomUUID;

public class Main {
     static void main(String[] args) {
        TicketService ticketService = new TicketService();

        SupportTicket ticket = ticketService.createTicket(
                randomUUID().toString(),
                "Payment API is returning 500"
        );

        ticketService.printTicketSummary(ticket);

        ticketService.openTicket(ticket);

        ticketService.printTicketUpdate(ticket);

        ticketService.startInvestigation(ticket);

        ticketService.printTicketSummary(ticket);
    }
}

