package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.port.outbound.UserRepository
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

@Singleton
@Requires(property = "persistence.type", value = "in-memory", defaultValue = "in-memory")
class InMemoryUserRepository : UserRepository {
    private val users = ConcurrentHashMap<TypedUUID<User>, PersistentUser>()

    override fun save(user: User) {
        users[user.id] = PersistentUser(
            id = user.id,
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber
        )
    }

    fun findById(id: TypedUUID<User>): PersistentUser? = users[id]
}

data class PersistentUser(
    val id: TypedUUID<User>,
    val name: String,
    val email: String,
    val phoneNumber: String
)
