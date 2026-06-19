package org.example.users.service.config

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.example.users.service.application.CreateUserService
import org.example.users.service.port.inbound.CreateUserUseCase

@Factory
class ServiceFactory {

    @Singleton
    fun createUserUseCase(): CreateUserUseCase {
        return CreateUserService()
    }
}
