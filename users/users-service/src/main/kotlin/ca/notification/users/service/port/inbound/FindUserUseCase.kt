package ca.notification.users.service.port.inbound

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User

interface FindUserUseCase {
    fun findById(id: TypedUUID<User>): User?
    fun findByEmail(email: String): User?
}
