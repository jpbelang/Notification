package ca.notification.users.service.application

import ca.notification.users.service.domain.User
import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.port.inbound.CreateUserUseCase

class CreateUserService : CreateUserUseCase {
    override fun execute(command: CreateUserUseCase.Command): User {
        return User(
            id = TypedUUID.create(),
            name = command.name,
            email = command.email,
            phoneNumber = command.phoneNumber
        )
    }
}
