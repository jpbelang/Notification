package ca.notification.users.service.port.outbound

import ca.notification.users.service.domain.NewUser
import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User

interface CredentialsRepository {
    fun save(user: NewUser): TypedUUID<User>
    fun update(user: User)
    fun delete(id: TypedUUID<User>)
}
