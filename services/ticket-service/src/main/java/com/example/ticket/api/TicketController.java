package com.example.ticket.api;

import com.example.ticket.api.dto.CreateTicketRequest;
import com.example.ticket.api.dto.TicketResponse;
import com.example.ticket.api.dto.UpdateTicketStatusRequest;
import com.example.ticket.application.TicketNotFoundException;
import com.example.ticket.application.TicketService;
import com.example.ticket.domain.Ticket;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        Ticket ticket = ticketService.createTicket(request.title(), request.customerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketResponse.from(ticket));
    }

    @GetMapping("/{id}")
    public TicketResponse getTicket(@PathVariable String id) {
        return TicketResponse.from(ticketService.getTicket(id));
    }

    @PatchMapping("/{id}/status")
    public TicketResponse updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateTicketStatusRequest request
    ) {
        try {
            return TicketResponse.from(ticketService.changeStatus(id, request.status()));
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @org.springframework.web.bind.annotation.ExceptionHandler(TicketNotFoundException.class)
    public String handleTicketNotFound(TicketNotFoundException exception) {
        return exception.getMessage();
    }
}
