package ca.notification.users.service.config

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import ca.notification.users.service.application.CreateUserService
import ca.notification.users.service.port.inbound.CreateUserUseCase

@Factory
class ServiceFactory {

    @Singleton
    fun createUserUseCase(): CreateUserUseCase {
        return CreateUserService()
    }
}
