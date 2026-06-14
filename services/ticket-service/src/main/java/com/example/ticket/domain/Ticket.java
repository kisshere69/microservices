package com.example.ticket.domain;

import java.time.Instant;
import java.util.Objects;

public class Ticket {

    private final String id;
    private final String title;
    private final String customerId;
    private final Instant createdAt;
    private TicketStatus status;

    private Ticket(String id, String title, String customerId, TicketStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public static Ticket create(String id, String title, String customerId) {
        return new Ticket(id, title, customerId, TicketStatus.NEW, Instant.now());
    }

    public void changeStatus(TicketStatus nextStatus) {
        if (!status.canMoveTo(nextStatus)) {
            throw new IllegalStateException("Cannot move ticket from " + status + " to " + nextStatus);
        }
        this.status = nextStatus;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCustomerId() {
        return customerId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
