package ca.notification.users.service.application

import ca.notification.users.service.domain.User
import ca.notification.users.service.domain.UserNotFoundException
import ca.notification.users.service.port.inbound.UpdateUserUseCase
import ca.notification.users.service.port.outbound.CredentialsRepository
import ca.notification.users.service.port.outbound.UserRepository

class UpdateUserService(
    private val userRepository: UserRepository,
    private val credentialsRepository: CredentialsRepository
) : UpdateUserUseCase {
    override fun execute(command: UpdateUserUseCase.Command): User {
        userRepository.findById(command.id)
            ?: throw UserNotFoundException("User with id ${command.id} not found")

        val updatedUser = User.from(
            id = command.id,
            name = command.name,
            email = command.email,
            phoneNumber = command.phoneNumber
        )

        userRepository.save(updatedUser)
        credentialsRepository.update(updatedUser)
        return updatedUser
    }
}
