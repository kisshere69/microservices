package com.example.ticket.application;

import com.example.ticket.domain.Ticket;
import com.example.ticket.domain.TicketStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TicketService {

    private final Map<String, Ticket> tickets = new ConcurrentHashMap<>();

    public Ticket createTicket(String title, String customerId) {
        Ticket ticket = Ticket.create(UUID.randomUUID().toString(), title, customerId);
        tickets.put(ticket.getId(), ticket);
        return ticket;
    }

    public Ticket getTicket(String id) {
        Ticket ticket = tickets.get(id);
        if (ticket == null) {
            throw new TicketNotFoundException(id);
        }
        return ticket;
    }

    public Ticket changeStatus(String id, TicketStatus status) {
        Ticket ticket = getTicket(id);
        ticket.changeStatus(status);
        return ticket;
    }
}
