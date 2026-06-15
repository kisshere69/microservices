# Microservices Support Ticket Platform

This repository is being shaped into a Java microservices project for managing
support tickets. The current implementation is still a small Java prototype;
the committed design describes the intended final architecture.

## Project Idea

The final system will let users create support tickets, track status changes,
assign tickets to the right team, send notifications, and keep an audit trail of
important lifecycle events.

The target architecture uses:

- Java 21 and Spring Boot for service implementation.
- Spring Cloud Gateway as the external API entry point.
- PostgreSQL for service-owned databases.
- Apache Kafka for event-driven communication.
- Docker and Docker Compose for local runtime infrastructure.
- Jenkins for CI/CD.
- JUnit 5, Mockito, Spring Boot Test, and Testcontainers for testing.

## Target Structure

```text
Microservices/
|-- docs/
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
|-- FINAL_PROJECT_DESIGN.md
`-- README.md
```

## Design Documents

- `FINAL_PROJECT_DESIGN.md` contains the full architecture and delivery plan.
- Future documents under `docs/` will describe architecture, API contracts,
  Kafka events, Jenkins CI/CD, deployment, and operations in more detail.

## Current Status

This repository currently contains the original Java prototype under
`src/com/example/support` plus the design structure for the future
microservices application. No service implementation code has been added yet.
