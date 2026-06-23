package ca.notification.users.service.port.inbound

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User

interface DeleteUserUseCase {
    fun execute(id: TypedUUID<User>)
}
