package ca.notification.users.service.application

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.port.inbound.FindUserUseCase
import ca.notification.users.service.port.outbound.UserRepository

class FindUserService(
    private val userRepository: UserRepository
) : FindUserUseCase {
    override fun findById(id: TypedUUID<User>): User? {
        return userRepository.findById(id)
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}
