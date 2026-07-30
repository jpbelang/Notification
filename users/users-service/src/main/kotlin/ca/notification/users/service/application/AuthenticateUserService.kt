package ca.notification.users.service.application

import ca.notification.users.service.domain.User
import ca.notification.users.service.port.inbound.AuthenticateUserUseCase
import ca.notification.users.service.port.outbound.CredentialsRepository
import ca.notification.users.service.port.outbound.UserRepository

class AuthenticateUserService(
    private val userRepository: UserRepository,
    private val credentialsRepository: CredentialsRepository
) : AuthenticateUserUseCase {
    override fun execute(command: AuthenticateUserUseCase.Command): User? {
        val user = userRepository.findByEmail(command.email) ?: return null
        val isAuthenticated = credentialsRepository.authenticate(command.email, command.password)
        return if (isAuthenticated) {
            user
        } else {
            null
        }
    }
}
