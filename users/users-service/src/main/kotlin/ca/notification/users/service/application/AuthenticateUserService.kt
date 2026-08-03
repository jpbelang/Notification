package ca.notification.users.service.application

import ca.notification.users.service.domain.AuthenticationResult
import ca.notification.users.service.port.inbound.AuthenticateUserUseCase
import ca.notification.users.service.port.outbound.CredentialsRepository
import ca.notification.users.service.port.outbound.UserRepository

class AuthenticateUserService(
    private val userRepository: UserRepository,
    private val credentialsRepository: CredentialsRepository
) : AuthenticateUserUseCase {
    override fun execute(command: AuthenticateUserUseCase.Command): AuthenticationResult? {
        val user = userRepository.findByEmail(command.email) ?: return null
        val tokens = credentialsRepository.authenticate(command.email, command.password)
        return tokens?.let {
            AuthenticationResult(user, it)
        }
    }
}
