package com.example.ticket.domain;

import java.util.Set;

public enum TicketStatus {
    NEW,
    OPEN,
    INVESTIGATING,
    WAITING_FOR_CUSTOMER,
    RESOLVED,
    CLOSED;

    public boolean canMoveTo(TicketStatus nextStatus) {
        return switch (this) {
            case NEW -> Set.of(OPEN).contains(nextStatus);
            case OPEN -> Set.of(INVESTIGATING, CLOSED).contains(nextStatus);
            case INVESTIGATING -> Set.of(WAITING_FOR_CUSTOMER, RESOLVED).contains(nextStatus);
            case WAITING_FOR_CUSTOMER -> Set.of(INVESTIGATING, CLOSED).contains(nextStatus);
            case RESOLVED -> Set.of(CLOSED, OPEN).contains(nextStatus);
            case CLOSED -> false;
        };
    }
}
