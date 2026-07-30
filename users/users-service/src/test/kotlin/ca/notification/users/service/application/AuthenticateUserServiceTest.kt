package ca.notification.users.service.application

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.port.inbound.AuthenticateUserUseCase
import ca.notification.users.service.port.outbound.CredentialsRepository
import ca.notification.users.service.port.outbound.UserRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AuthenticateUserServiceTest : StringSpec({

    val userRepository = mockk<UserRepository>()
    val credentialsRepository = mockk<CredentialsRepository>()
    val service = AuthenticateUserService(userRepository, credentialsRepository)

    beforeTest {
        clearAllMocks()
    }

    "should return user when credentials are correct" {
        val email = "test@example.com"
        val password = "password"
        val user = User.from(TypedUUID.create(), "Test User", email, "123")

        every { credentialsRepository.authenticate(email, password) } returns true
        every { userRepository.findByEmail(email) } returns user

        val command = AuthenticateUserUseCase.Command(email, password)
        val result = service.execute(command)

        result shouldBe user
        verify { credentialsRepository.authenticate(email, password) }
        verify { userRepository.findByEmail(email) }
    }

    "should return null when credentials are incorrect" {
        val email = "test@example.com"
        val password = "wrong-password"

        every { credentialsRepository.authenticate(email, password) } returns false

        val command = AuthenticateUserUseCase.Command(email, password)
        val result = service.execute(command)

        result shouldBe null
        verify { credentialsRepository.authenticate(email, password) }
        verify(exactly = 0) { userRepository.findByEmail(any()) }
    }

    "should return null when user is authenticated but not found in user repository" {
        val email = "test@example.com"
        val password = "password"

        every { credentialsRepository.authenticate(email, password) } returns true
        every { userRepository.findByEmail(email) } returns null

        val command = AuthenticateUserUseCase.Command(email, password)
        val result = service.execute(command)

        result shouldBe null
        verify { credentialsRepository.authenticate(email, password) }
        verify { userRepository.findByEmail(email) }
    }
})
