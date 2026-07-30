package ca.notification.users.service.port.inbound

import ca.notification.users.service.domain.User

interface AuthenticateUserUseCase {
    fun execute(command: Command): User?

    data class Command(
        val email: String,
        val password: String
    )
}
