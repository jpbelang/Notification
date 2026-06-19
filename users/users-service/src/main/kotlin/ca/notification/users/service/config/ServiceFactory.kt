package ca.notification.users.service.config

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import ca.notification.users.service.application.CreateUserService
import ca.notification.users.service.port.inbound.CreateUserUseCase
import ca.notification.users.service.port.outbound.UserRepository

@Factory
class ServiceFactory {

    @Singleton
    fun createUserUseCase(userRepository: UserRepository): CreateUserUseCase {
        return CreateUserService(userRepository)
    }
}
