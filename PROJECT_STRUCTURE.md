# Project Structure

This repository currently contains a small Java support-ticket service prototype.
The code is organized as a single IntelliJ Java module with source files under
`src/com/example/support`.

## Repository Layout

```text
Microservices/
├── .gitignore
├── LICENSE
├── Microservices.iml
├── PROJECT_STRUCTURE.md
└── src/
    └── com/
        └── example/
            └── support/
                ├── Main.java
                ├── SupportTicket.java
                └── TicketService.java
```

## Source Package Map

```mermaid
flowchart TD
    root["Microservices"]
    module["Microservices.iml<br/>IntelliJ Java module"]
    src["src/"]
    package["com.example.support"]
    main["Main.java<br/>Demo entry point"]
    service["TicketService.java<br/>Ticket use-case operations"]
    model["SupportTicket.java<br/>Ticket state model"]

    root --> module
    root --> src
    src --> package
    package --> main
    package --> service
    package --> model
```

## Current Design

```mermaid
classDiagram
    class Main {
        static void main(String[] args)
    }

    class TicketService {
        +SupportTicket createTicket(String id, String title)
        +void openTicket(SupportTicket ticket)
        +void printTicketUpdate(SupportTicket ticket)
        +void startInvestigation(SupportTicket ticket)
        +void printTicketSummary(SupportTicket ticket)
    }

    class SupportTicket {
        -String id
        -String title
        -String status
        +SupportTicket(String id, String title)
        +void openTicket(String status1)
        +void printUpdate()
        +void changeStatus(String status2)
        +void printSummary()
    }

    Main --> TicketService : uses
    TicketService --> SupportTicket : creates and updates
```

## Runtime Flow

```mermaid
sequenceDiagram
    participant Main
    participant TicketService
    participant SupportTicket

    Main->>TicketService: createTicket(id, title)
    TicketService->>SupportTicket: new SupportTicket(id, title)
    SupportTicket-->>TicketService: ticket with NEW status
    TicketService-->>Main: ticket

    Main->>TicketService: printTicketSummary(ticket)
    TicketService->>SupportTicket: printSummary()

    Main->>TicketService: openTicket(ticket)
    TicketService->>SupportTicket: openTicket("OPEN")

    Main->>TicketService: printTicketUpdate(ticket)
    TicketService->>SupportTicket: printUpdate()

    Main->>TicketService: startInvestigation(ticket)
    TicketService->>SupportTicket: changeStatus("INVESTIGATING")

    Main->>TicketService: printTicketSummary(ticket)
    TicketService->>SupportTicket: printSummary()
```

## Component Responsibilities

| File | Responsibility |
| --- | --- |
| `Main.java` | Demonstrates the ticket lifecycle by creating a ticket, printing it, opening it, and moving it into investigation. |
| `TicketService.java` | Provides the application-level operations for creating and updating support tickets. |
| `SupportTicket.java` | Stores ticket data and owns the current ticket status. |

## Design Notes

- The current code is a single Java module, not yet split into separately deployed services.
- `TicketService` acts as the service layer around the `SupportTicket` domain object.
- `SupportTicket` currently prints directly to standard output; future service boundaries may move presentation and logging outside the domain model.
- The ticket lifecycle currently uses string statuses: `NEW`, `OPEN`, and `INVESTIGATING`.
