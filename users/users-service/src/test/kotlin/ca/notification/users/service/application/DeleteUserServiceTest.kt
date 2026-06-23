package ca.notification.users.service.application

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.domain.UserNotFoundException
import ca.notification.users.service.port.outbound.CredentialsRepository
import ca.notification.users.service.port.outbound.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class DeleteUserServiceTest : StringSpec({

    val userRepository = mockk<UserRepository>()
    val credentialsRepository = mockk<CredentialsRepository>()
    val service = DeleteUserService(userRepository, credentialsRepository)

    beforeTest {
        clearMocks(userRepository, credentialsRepository)
    }

    "should delete existing user" {
        val userId = TypedUUID.create<User>()
        val user = User.from(userId, "John", "john@example.com", "555")

        every { userRepository.findById(userId) } returns user
        every { userRepository.delete(user) } returns Unit
        every { credentialsRepository.delete(userId) } returns Unit

        service.execute(userId)

        verify { userRepository.delete(user) }
        verify { credentialsRepository.delete(userId) }
    }

    "should throw exception if user not found" {
        val userId = TypedUUID.create<User>()

        every { userRepository.findById(userId) } returns null

        shouldThrow<UserNotFoundException> {
            service.execute(userId)
        }

        verify(exactly = 0) { userRepository.delete(any()) }
        verify(exactly = 0) { credentialsRepository.delete(any()) }
    }
})
