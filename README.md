# Notification

[![Build and Test](https://github.com/jpbelang/Notification/actions/workflows/build.yml/badge.svg)](https://github.com/jpbelang/Notification/actions/workflows/build.yml)
![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

## Project Overview

This is a multi-service event-driven application designed to handle notifications, user management, and organizational structures. It follows **Hexagonal Architecture** principles and is built using a modern Kotlin/JVM stack, deployed on AWS using Serverless components.

## Tech Stack

- **Language:** Kotlin 2.1.x
- **Platform:** JVM 23
- **Framework:** Micronaut 4.7.x
- **Build System:** Gradle 9.6.1 (Kotlin DSL)
- **Persistence:** DynamoDB (Single-table design)
- **Authentication:** AWS Cognito (JWT tokens returned as Http-only cookies)
- **Messaging:** AWS EventBridge (`NotificationBus`) & SQS
- **Infrastructure:** AWS CDK (Kotlin)
- **Testing:** Kotest, MockK, Micronaut Test

## Architectural Principles

- **Hexagonal Architecture:** Business logic is kept pure and separated from infrastructure concerns via ports and adapters.
- **Constructor Injection:** DI is managed via `ServiceFactory` classes, minimizing Micronaut annotations in core and infrastructure logic.
- **Single Table Design:** Each domain service (Users, Organisations) uses its own DynamoDB table with a single-table layout for efficient data retrieval.
- **Event-Driven:** Services communicate asynchronously via the shared `NotificationBus`.

## Project Structure

The project is organized into several modules:

- **`base-infrastructure`**: Shared AWS resources, primarily the `NotificationBus` (EventBridge).
- **`users`**: User management domain.
  - `users-service`: Micronaut-based service for user CRUD and authentication.
  - `users-infrastructure`: CDK code for deploying the user service, Cognito, and event consumers.
- **`organisations`**: Organisation management domain.
  - `organisations-service`: Micronaut-based service for managing organisations and participants.
  - `organisations-infrastructure`: CDK code for the organisation service and its DynamoDB table.
- **`utils`**: Shared Kotlin utilities and common logic.
- **`app`**: The application entry point.

## Event-Driven Integration

The `organisations-service` publishes lifecycle events (e.g., `NewOrganisation`, `OrganisationParticipantAdded`) to the shared `NotificationBus`. 

The `users-service` consumes all organization-related events via an SQS queue and an EventBridge rule. These events are dispatched to `ProcessOrganisationNotificationService` for downstream processing.

## Development & Building

### Prerequisites
- JDK 23
- AWS CLI configured
- Node.js & CDK CLI installed (`npm install -g aws-cdk`)

### Common Gradle Tasks
Use the Gradle Wrapper (`./gradlew`) for consistent builds:
- `./gradlew build`: Build all subprojects.
- `./gradlew check`: Run all tests and quality checks.
- `./gradlew clean`: Remove all build artifacts.

Note: The project uses a version catalog (`gradle/libs.versions.toml`) and shared build logic in `buildSrc`.

### Infrastructure Deployment (CDK)

Each infrastructure module contains its own `cdk.json` and can be synthesized independently:

```bash
# Example for users infrastructure
cd users/users-infrastructure
npx cdk synth
```

Infrastructure modules depend on `:base-infrastructure` to access shared resource information like the `NotificationBus` name.

---
"A beginning is the time for taking the most delicate care that the balances are correct." - Dune
