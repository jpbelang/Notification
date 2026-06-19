package ca.notification.users.service.port.inbound

import ca.notification.users.service.domain.User

interface CreateUserUseCase {
    fun execute(command: Command): User

    data class Command(
        val name: String,
        val email: String,
        val phoneNumber: String,
        val password: String
    )
}
