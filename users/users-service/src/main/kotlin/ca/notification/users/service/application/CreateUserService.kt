package ca.notification.users.service.application

import ca.notification.users.service.domain.User
import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.port.inbound.CreateUserUseCase
import ca.notification.users.service.port.outbound.UserRepository
import ca.notification.users.service.port.outbound.CredentialsRepository

class CreateUserService(
    private val userRepository: UserRepository,
    private val credentialsRepository: CredentialsRepository
) : CreateUserUseCase {
    override fun execute(command: CreateUserUseCase.Command): User {
        val user = User(
            id = TypedUUID.create(),
            name = command.name,
            email = command.email,
            phoneNumber = command.phoneNumber,
            password = command.password
        )
        userRepository.save(user)
        credentialsRepository.save(user)
        return user
    }
}
