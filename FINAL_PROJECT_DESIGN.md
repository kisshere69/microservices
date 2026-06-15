# Final Project Design

This document describes the intended final shape of the Microservices project as
a support-ticket platform built with Java, Spring Boot, Kafka, PostgreSQL,
Docker, and Jenkins.

This is a design artifact only. The current update prepares folders and
documentation, but does not add implementation code.

## Product Result

The final project should allow users to create support tickets, track their
status, assign them for investigation, and receive updates as the ticket moves
through its lifecycle.

The current Java prototype models the core ticket behavior. The final design
expands that behavior into independently deployable services with clear data
ownership, REST API boundaries, Kafka events, and Jenkins-based CI/CD.

## Target Repository Shape

```text
Microservices/
|-- docs/
|   |-- architecture.md
|   |-- api-contracts.md
|   |-- kafka-events.md
|   |-- ci-cd.md
|   |-- deployment.md
|   `-- operations.md
|-- frontend/
|   |-- index.html
|   |-- styles.css
|   `-- app.js
|-- gateway/
|-- services/
|   |-- ticket-service/
|   |-- customer-service/
|   |-- assignment-service/
|   |-- notification-service/
|   `-- audit-service/
|-- infrastructure/
|   |-- docker/
|   |-- kafka/
|   |-- postgres/
|   |-- jenkins/
|   `-- observability/
|-- tests/
|   |-- contract/
|   `-- integration/
|-- README.md
`-- FINAL_PROJECT_DESIGN.md
```

## Technology Stack

| Area | Technology |
| --- | --- |
| Language | Java 21 |
| Service framework | Spring Boot |
| API layer | Spring Web, Spring Cloud Gateway |
| Data access | Spring Data JPA |
| Database | PostgreSQL |
| Messaging | Apache Kafka |
| Security | Spring Security, JWT |
| Build tool | Maven |
| Local runtime | Docker, Docker Compose |
| CI/CD | Jenkins |
| Testing | JUnit 5, Mockito, Spring Boot Test, Testcontainers |
| Observability | Spring Boot Actuator, Micrometer, OpenTelemetry |

## Service Architecture

```mermaid
flowchart LR
    client["Client App"]
    gateway["API Gateway"]
    ticket["Ticket Service"]
    customer["Customer Service"]
    assignment["Assignment Service"]
    notification["Notification Service"]
    audit["Audit Service"]
    kafka["Apache Kafka"]

    ticketDb[("Ticket DB")]
    customerDb[("Customer DB")]
    assignmentDb[("Assignment DB")]
    auditDb[("Audit DB")]

    client --> gateway
    gateway --> ticket
    gateway --> customer
    gateway --> assignment

    ticket --> ticketDb
    customer --> customerDb
    assignment --> assignmentDb
    audit --> auditDb

    ticket --> kafka
    customer --> kafka
    assignment --> kafka
    kafka --> notification
    kafka --> audit
```

## Service Responsibilities

| Service | Responsibility | Owns Data |
| --- | --- | --- |
| API Gateway | Routes external requests, applies request validation, and provides a stable public API surface. | No |
| Ticket Service | Creates tickets, stores ticket state, and manages lifecycle transitions. | Yes |
| Customer Service | Stores customer profiles and contact preferences. | Yes |
| Assignment Service | Assigns tickets to teams or agents based on rules and workload. | Yes |
| Notification Service | Consumes Kafka events and sends status updates through configured channels. | No |
| Audit Service | Records ticket lifecycle events for traceability and reporting. | Yes |

## Ticket Lifecycle

```mermaid
stateDiagram-v2
    [*] --> NEW
    NEW --> OPEN: ticket opened
    OPEN --> INVESTIGATING: investigation started
    INVESTIGATING --> WAITING_FOR_CUSTOMER: more information needed
    WAITING_FOR_CUSTOMER --> INVESTIGATING: customer replies
    INVESTIGATING --> RESOLVED: fix or answer provided
    RESOLVED --> CLOSED: customer confirms or timeout expires
    RESOLVED --> OPEN: issue reopens
    CLOSED --> [*]
```

## Primary Request Flow

```mermaid
sequenceDiagram
    participant User
    participant Gateway as API Gateway
    participant Ticket as Ticket Service
    participant Kafka
    participant Assign as Assignment Service
    participant Notify as Notification Service
    participant Audit as Audit Service

    User->>Gateway: Create support ticket
    Gateway->>Ticket: POST /tickets
    Ticket-->>Gateway: Ticket created
    Ticket->>Kafka: Publish TicketCreated
    Kafka->>Assign: Consume TicketCreated
    Kafka->>Notify: Consume TicketCreated
    Kafka->>Audit: Consume TicketCreated
    Gateway-->>User: Ticket id and NEW status

    Assign->>Kafka: Publish TicketAssigned
    Kafka->>Ticket: Consume TicketAssigned
    Kafka->>Notify: Consume TicketAssigned
    Kafka->>Audit: Consume TicketAssigned
```

## High-Level API Surface

| Endpoint | Owner | Purpose |
| --- | --- | --- |
| `POST /tickets` | Ticket Service | Create a support ticket. |
| `GET /tickets/{id}` | Ticket Service | Read ticket details and current status. |
| `PATCH /tickets/{id}/status` | Ticket Service | Move a ticket through its lifecycle. |
| `POST /tickets/{id}/comments` | Ticket Service | Add a user or agent update to a ticket. |
| `GET /customers/{id}` | Customer Service | Read customer profile data. |
| `POST /assignments` | Assignment Service | Assign a ticket to an owner or queue. |

## Kafka Event Contracts

| Event | Producer | Consumers |
| --- | --- | --- |
| `TicketCreated` | Ticket Service | Assignment Service, Notification Service, Audit Service |
| `TicketStatusChanged` | Ticket Service | Notification Service, Audit Service |
| `TicketAssigned` | Assignment Service | Ticket Service, Notification Service, Audit Service |
| `CustomerUpdated` | Customer Service | Ticket Service, Notification Service, Audit Service |

Events should include a stable event id, timestamp, producer name, aggregate id,
event type, and schema version.

## Kafka Topics

| Topic | Producers | Consumers |
| --- | --- | --- |
| `ticket-events` | Ticket Service | Assignment Service, Notification Service, Audit Service |
| `assignment-events` | Assignment Service | Ticket Service, Notification Service, Audit Service |
| `customer-events` | Customer Service | Ticket Service, Notification Service, Audit Service |
| `audit-events` | Services that need explicit audit records | Audit Service |

## Data Ownership Rules

- Each service owns its database and exposes access through APIs or events.
- No service should read or write another service database directly.
- Cross-service workflows should use events for asynchronous updates.
- User-facing reads can be composed by the API Gateway or a dedicated query view
  when the project needs faster dashboard-style screens.

## Jenkins CI/CD Design

```mermaid
flowchart TD
    repo["GitHub main branch"]
    webhook["GitHub webhook"]
    jenkins["Jenkins pipeline"]
    test["Build and tests"]
    image["Container images"]
    registry["Container registry"]
    env["Runtime environment"]
    obs["Logs, metrics, traces"]

    repo --> webhook
    webhook --> jenkins
    jenkins --> test
    test --> image
    image --> registry
    registry --> env
    env --> obs
```

Jenkins pipeline stages:

1. Checkout from GitHub.
2. Validate project structure.
3. Build each service.
4. Run unit tests.
5. Run integration tests with Testcontainers.
6. Build Docker images.
7. Push Docker images to the registry.
8. Deploy to the selected environment.

Target deployment characteristics:

- Each service builds and deploys independently.
- Database migrations are versioned per service.
- Configuration is supplied through environment variables or a secret manager.
- Logs include correlation ids so one request can be traced across services.
- Health checks expose readiness and liveness separately.

## Security Design

- External traffic enters through the API Gateway.
- Authentication is verified at the gateway before requests reach services.
- Services authorize actions based on user role and ticket ownership.
- Secrets are never stored in source control.
- Audit events are immutable from the application path.

## Testing Strategy

| Test Type | Goal |
| --- | --- |
| Unit tests | Verify domain rules inside each service. |
| Contract tests | Verify API and event compatibility between services. |
| Integration tests | Verify service behavior with real database and broker dependencies. |
| End-to-end tests | Verify critical user journeys across the deployed system. |

## Final Definition of Done

- Users can create, view, update, assign, and close support tickets.
- Ticket state transitions are validated and auditable.
- Notifications are produced from Kafka ticket and assignment events.
- Each service has clear ownership of its data and API surface.
- The system can be built, tested, and deployed from the main branch through Jenkins.
- Architecture, API contracts, deployment, and operations are documented before
  production use.
