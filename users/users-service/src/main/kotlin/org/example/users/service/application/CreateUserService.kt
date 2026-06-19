package org.example.users.service.application

import org.example.users.service.domain.User
import org.example.users.service.domain.TypedUUID
import org.example.users.service.port.inbound.CreateUserUseCase

class CreateUserService : CreateUserUseCase {
    override fun execute(command: CreateUserUseCase.Command): User {
        return User(
            id = TypedUUID.create(),
            name = command.name,
            email = command.email
        )
    }
}
