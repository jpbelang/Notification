# Project Guidelines: Notification

## Tech Stack
- **Language:** Kotlin
- **Platform:** JVM (Target Version: 23)
- **Build System:** Gradle (Kotlin DSL)
- **Testing:** Kotest
- **Dependency Injection:** Micronaut, with as few annotations as possible.

## Architectural style
- Hexagonal architecture

## Coding Standards
- Follow official Kotlin coding conventions.
- Prefer immutability: use `val` over `var` where possible.
- Use Kotlin standard library functions (e.g., `apply`, `also`, `let`, `run`) for concise code.
- Ensure all new logic is covered by unit tests.

## Project Structure
- Multi-module setup:
  - `app`: Application entry point.
  - `utils`: Shared utility functions.
  - `users`: User management domain.
    - `users-infrastructure`: External integrations and persistence.
    - `users-service`: Business logic.

## Gradle Conventions
- Shared build logic is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
- Use the version catalog in `gradle/libs.versions.toml` for dependency management.
