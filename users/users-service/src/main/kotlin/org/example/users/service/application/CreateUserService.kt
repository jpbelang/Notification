package org.example.users.service.application

import org.example.users.service.domain.User
import org.example.users.service.port.inbound.CreateUserUseCase
import java.util.*

class CreateUserService : CreateUserUseCase {
    override fun execute(command: CreateUserUseCase.Command): User {
        return User(
            id = UUID.randomUUID(),
            name = command.name,
            email = command.email
        )
    }
}
