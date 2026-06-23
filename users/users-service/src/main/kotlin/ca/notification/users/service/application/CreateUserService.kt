package ca.notification.users.service.application

import ca.notification.users.service.domain.NewUser
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
        val newUser = User.createNew(
            name = command.name,
            email = command.email,
            phoneNumber = command.phoneNumber,
            password = command.password
        )
        val actualId = credentialsRepository.save(newUser)
        val user = User.withId(newUser, actualId)
        userRepository.save(user)
        return user
    }
}
