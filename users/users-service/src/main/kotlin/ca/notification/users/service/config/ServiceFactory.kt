package ca.notification.users.service.config

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import ca.notification.users.service.application.CreateUserService
import ca.notification.users.service.port.inbound.CreateUserUseCase
import ca.notification.users.service.port.outbound.UserRepository
import ca.notification.users.service.port.outbound.CredentialsRepository

@Factory
class ServiceFactory {

    @Singleton
    fun createUserUseCase(
        userRepository: UserRepository,
        credentialsRepository: CredentialsRepository
    ): CreateUserUseCase {
        return CreateUserService(userRepository, credentialsRepository)
    }
}
