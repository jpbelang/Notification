package ca.notification.users.service.port.inbound

import ca.notification.users.service.domain.AuthenticationResult

interface AuthenticateUserUseCase {
    fun execute(command: Command): AuthenticationResult?

    data class Command(
        val email: String,
        val password: String
    )
}
