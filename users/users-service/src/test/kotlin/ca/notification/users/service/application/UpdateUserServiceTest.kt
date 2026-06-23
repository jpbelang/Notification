package ca.notification.users.service.application

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.domain.UserNotFoundException
import ca.notification.users.service.port.inbound.UpdateUserUseCase
import ca.notification.users.service.port.outbound.CredentialsRepository
import ca.notification.users.service.port.outbound.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class UpdateUserServiceTest : StringSpec({

    val userRepository = mockk<UserRepository>()
    val credentialsRepository = mockk<CredentialsRepository>()
    val service = UpdateUserService(userRepository, credentialsRepository)

    "should update existing user" {
        val userId = TypedUUID.create<User>()
        val existingUser = User.from(userId, "Old Name", "old@example.com", "123")
        
        every { userRepository.findById(userId) } returns existingUser
        every { userRepository.save(any()) } returns Unit
        every { credentialsRepository.update(any()) } returns Unit

        val command = UpdateUserUseCase.Command(
            id = userId,
            name = "New Name",
            email = "old@example.com",
            phoneNumber = "456"
        )

        val result = service.execute(command)

        result.name shouldBe "New Name"
        result.phoneNumber shouldBe "456"
        result.email shouldBe "old@example.com"

        verify { userRepository.save(result) }
        verify { credentialsRepository.update(result) }
        verify(exactly = 0) { userRepository.delete(any()) }
    }

    "should handle email change" {
        val userId = TypedUUID.create<User>()
        val existingUser = User.from(userId, "Name", "old@example.com", "123")
        
        every { userRepository.findById(userId) } returns existingUser
        every { userRepository.save(any()) } returns Unit
        every { credentialsRepository.update(any()) } returns Unit

        val command = UpdateUserUseCase.Command(
            id = userId,
            name = "Name",
            email = "new@example.com",
            phoneNumber = "123"
        )

        val result = service.execute(command)

        result.email shouldBe "new@example.com"

        verify(exactly = 0) { userRepository.delete(any()) }
        verify { userRepository.save(result) }
        verify { credentialsRepository.update(result) }
    }

    "should throw exception if user not found" {
        val userId = TypedUUID.create<User>()
        every { userRepository.findById(userId) } returns null

        val command = UpdateUserUseCase.Command(
            id = userId,
            name = "Name",
            email = "email@example.com",
            phoneNumber = "123"
        )

        shouldThrow<UserNotFoundException> {
            service.execute(command)
        }
    }
})
