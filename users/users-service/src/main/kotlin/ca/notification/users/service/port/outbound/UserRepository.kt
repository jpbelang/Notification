package ca.notification.users.service.port.outbound

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User

interface UserRepository {
    fun save(user: User)
    fun findById(id: TypedUUID<User>): User?
    fun findByEmail(email: String): User?
    fun delete(user: User)
}
