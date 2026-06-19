package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.port.outbound.CredentialsRepository
import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

@Singleton
class InMemoryCredentialsRepository : CredentialsRepository {
    private val credentials = ConcurrentHashMap<TypedUUID<User>, UserCredentials>()

    override fun save(user: User) {
        credentials[user.id] = UserCredentials(
            userId = user.id,
            email = user.email,
            password = user.password
        )
    }

    fun findByUserId(userId: TypedUUID<User>): UserCredentials? = credentials[userId]
}

data class UserCredentials(
    val userId: TypedUUID<User>,
    val email: String,
    val password: String
)
