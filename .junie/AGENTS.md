# Project Guidelines: Notification

## Tech Stack
- **Language:** Kotlin
- **Platform:** JVM (Target Version: 23)
- **Build System:** Gradle (Kotlin DSL)
- **Testing:** Kotest
- **Dependency Injection:** Micronaut, always using constructor injection when possible

## Architectural style
- Hexagonal architecture
- Keep the core business logic free of Micronaut dependencies.
- Try to keep the micronaut in a micronaut package.  

## Coding Standards
- Follow official Kotlin coding conventions.
- Prefer immutability: use `val` over `var` where possible.
- Use Kotlin standard library functions (e.g., `apply`, `also`, `let`, `run`) for concise code.
- Ensure all new logic is covered by unit tests.
- if the http schema changes, update the corresponding intellij http tests for the affected service.
- Micronaut annotations should only be used in micronaut factories, unless absolutely necessary.

## Project Structure
- Multi-module setup:
  - `app`: Application entry point.
  - `utils`: Shared utility functions.
  - `users`: User management domain.
    - `users-infrastructure`: External integrations and persistence.
    - `users-service`: Business logic.
  - Single table design per service.

## Gradle Conventions
- Shared build logic is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
- Use the version catalog in `gradle/libs.versions.toml` for dependency management.

## Commit Messages
- Use imperative mood (e.g., "Add feature X" instead of "Added feature X").
- Keep messages concise and meaningful.
- Postfix them with a short quote from The Lord of the Rings of Dune.