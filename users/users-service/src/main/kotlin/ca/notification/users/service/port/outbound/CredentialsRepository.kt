package ca.notification.users.service.port.outbound

import ca.notification.users.service.domain.User

interface CredentialsRepository {
    fun save(user: User)
}
