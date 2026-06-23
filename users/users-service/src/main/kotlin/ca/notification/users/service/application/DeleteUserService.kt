package ca.notification.users.service.application

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.domain.UserNotFoundException
import ca.notification.users.service.port.inbound.DeleteUserUseCase
import ca.notification.users.service.port.outbound.CredentialsRepository
import ca.notification.users.service.port.outbound.UserRepository

class DeleteUserService(
    private val userRepository: UserRepository,
    private val credentialsRepository: CredentialsRepository
) : DeleteUserUseCase {
    override fun execute(id: TypedUUID<User>) {
        val user = userRepository.findById(id)
            ?: throw UserNotFoundException("User with id $id not found")
        
        userRepository.delete(user)
        credentialsRepository.delete(id)
    }
}
