package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.*
import ca.notification.users.service.port.outbound.CredentialsRepository
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

@Singleton
@Requires(property = "micronaut.environment", value = "local", defaultValue = "local")
class InMemoryCredentialsRepository : CredentialsRepository {
    private val credentials = ConcurrentHashMap<TypedUUID<User>, UserCredentials>()

    override fun save(user: NewUser): TypedUUID<User> {
        if (credentials.values.any { it.email == user.email }) {
            throw UserExistsException("User with email ${user.email} already exists")
        }
        val userId = TypedUUID.create<User>()
        credentials[userId] = UserCredentials(
            userId = userId,
            email = user.email,
            password = user.password
        )
        return userId
    }

    override fun update(user: User) {
        if (credentials.values.any { it.email == user.email && it.userId != user.id }) {
            throw UserExistsException("User with email ${user.email} already exists")
        }
        val existing = credentials[user.id] ?: return
        credentials[user.id] = existing.copy(email = user.email)
    }

    override fun delete(id: TypedUUID<User>) {
        credentials.remove(id)
    }

    override fun authenticate(email: String, password: String): AuthTokens? {
        val authenticated = credentials.values.any { it.email == email && it.password == password }
        return if (authenticated) {
            AuthTokens(
                accessToken = "mock-access-token",
                idToken = "mock-id-token",
                refreshToken = "mock-refresh-token",
                expiresIn = 3600
            )
        } else {
            null
        }
    }

    fun findByUserId(userId: TypedUUID<User>): UserCredentials? = credentials[userId]
}

data class UserCredentials(
    val userId: TypedUUID<User>,
    val email: String,
    val password: String
)
