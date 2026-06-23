package ca.notification.users.service.port.inbound

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User

interface UpdateUserUseCase {
    fun execute(command: Command): User

    data class Command(
        val id: TypedUUID<User>,
        val name: String,
        val email: String,
        val phoneNumber: String
    )
}
