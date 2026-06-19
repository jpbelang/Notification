package ca.notification.users.service.application

import ca.notification.users.service.domain.User
import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.port.inbound.CreateUserUseCase
import ca.notification.users.service.port.outbound.UserRepository

class CreateUserService(private val userRepository: UserRepository) : CreateUserUseCase {
    override fun execute(command: CreateUserUseCase.Command): User {
        val user = User(
            id = TypedUUID.create(),
            name = command.name,
            email = command.email,
            phoneNumber = command.phoneNumber,
            password = command.password
        )
        userRepository.save(user)
        return user
    }
}
