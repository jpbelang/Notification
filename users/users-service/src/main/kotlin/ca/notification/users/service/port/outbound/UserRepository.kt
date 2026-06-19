package ca.notification.users.service.port.outbound

import ca.notification.users.service.domain.User

interface UserRepository {
    fun save(user: User)
}
