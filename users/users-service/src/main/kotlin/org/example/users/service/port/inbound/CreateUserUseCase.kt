package org.example.users.service.port.inbound

import org.example.users.service.domain.User

interface CreateUserUseCase {
    fun execute(command: Command): User

    data class Command(
        val name: String,
        val email: String
    )
}
