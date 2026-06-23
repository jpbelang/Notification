package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.domain.UserExistsException
import ca.notification.users.service.port.outbound.UserRepository
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

@Singleton
@Requires(property = "micronaut.environment", value = "local", defaultValue = "local")
class InMemoryUserRepository : UserRepository {
    private val users = ConcurrentHashMap<TypedUUID<User>, PersistentUser>()

    override fun save(user: User) {
        if (users.values.any { it.email == user.email && it.id != user.id }) {
            throw UserExistsException("User with email ${user.email} already exists")
        }
        users[user.id] = PersistentUser(
            id = user.id,
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber
        )
    }

    override fun findById(id: TypedUUID<User>): User? = users[id]?.toDomain()

    override fun findByEmail(email: String): User? = users.values.find { it.email == email }?.toDomain()

    override fun delete(user: User) {
        users.remove(user.id)
    }

    private fun PersistentUser.toDomain() = User.from(id, name, email, phoneNumber)
}

data class PersistentUser(
    val id: TypedUUID<User>,
    val name: String,
    val email: String,
    val phoneNumber: String
)
