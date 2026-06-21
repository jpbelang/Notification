# Notification
 
 [![Build and Test](https://github.com/jpbelang/Notification/actions/workflows/build.yml/badge.svg)](https://github.com/jpbelang/Notification/actions/workflows/build.yml)
 ![Coverage](.github/badges/coverage.svg)
 ![Branches](.github/badges/branches.svg)
 
 This project uses [Gradle](https://gradle.org/).
To build and run the application, use the *Gradle* tool window by clicking the Gradle icon in the right-hand toolbar,
or run it directly from the terminal:

* Run `./gradlew run` to build and run the application.
* Run `./gradlew build` to only build the application.
* Run `./gradlew check` to run all checks, including tests.
* Run `./gradlew clean` to clean all build outputs.

Note the usage of the Gradle Wrapper (`./gradlew`).
This is the suggested way to use Gradle in production projects.

[Learn more about the Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html).

[Learn more about Gradle tasks](https://docs.gradle.org/current/userguide/command_line_interface.html#common_tasks).

This project follows the suggested multi-module setup and consists of the `app`, `utils`, and `users` subprojects.
The `users` project contains the `users-infrastructure` and `users-service` submodules.
`users-infrastructure` includes an AWS CDK setup in Kotlin for infrastructure management.
The shared build logic was extracted to a convention plugin located in `buildSrc`.

### AWS CDK Setup
The infrastructure for the `users` service is defined using AWS CDK in the `users-infrastructure` module.
To use it, you need to have `node` and the AWS CDK CLI installed.

- **CDK App:** `users/users-infrastructure/src/main/kotlin/ca/notification/users/infrastructure/cdk/UsersInfrastructureApp.kt`
- **CDK Stack:** `users/users-infrastructure/src/main/kotlin/ca/notification/users/infrastructure/cdk/UsersInfrastructureStack.kt`
- **Configuration:** `users/users-infrastructure/cdk.json`

To synthesize the CloudFormation template:
```bash
cd users/users-infrastructure
cdk synth
```
(This will use `./gradlew -q :users:users-infrastructure:run` as the executable command).

This project uses a version catalog (see `gradle/libs.versions.toml`) to declare and version dependencies,
a `.junie/AGENTS.md` file for project-specific guidelines,
and both a build cache and a configuration cache (see `gradle.properties`).