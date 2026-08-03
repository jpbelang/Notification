package ca.notification.users.service.application

import ca.notification.users.service.domain.AuthTokens
import ca.notification.users.service.domain.AuthenticationResult
import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.port.inbound.AuthenticateUserUseCase
import ca.notification.users.service.port.outbound.CredentialsRepository
import ca.notification.users.service.port.outbound.UserRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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

    "should return result when credentials are correct" {
        val email = "test@example.com"
        val password = "password"
        val user = User.from(TypedUUID.create(), "Test User", email, "123")
        val tokens = AuthTokens("access", "id", "refresh", 3600)

        every { userRepository.findByEmail(email) } returns user
        every { credentialsRepository.authenticate(email, password) } returns tokens

        val command = AuthenticateUserUseCase.Command(email, password)
        val result = service.execute(command)

        result shouldNotBe null
        result?.user shouldBe user
        result?.tokens shouldBe tokens
        verify { credentialsRepository.authenticate(email, password) }
        verify { userRepository.findByEmail(email) }
    }

    "should return null when credentials are incorrect" {
        val email = "test@example.com"
        val password = "wrong-password"
        val user = User.from(TypedUUID.create(), "Test User", email, "123")

        every { userRepository.findByEmail(email) } returns user
        every { credentialsRepository.authenticate(email, password) } returns null

        val command = AuthenticateUserUseCase.Command(email, password)
        val result = service.execute(command)

        result shouldBe null
        verify { userRepository.findByEmail(email) }
        verify { credentialsRepository.authenticate(email, password) }
    }

    "should return null when user does not exist" {
        val email = "nonexistent@example.com"
        val password = "any-password"

        every { userRepository.findByEmail(email) } returns null

        val command = AuthenticateUserUseCase.Command(email, password)
        val result = service.execute(command)

        result shouldBe null
        verify { userRepository.findByEmail(email) }
        verify(exactly = 0) { credentialsRepository.authenticate(any(), any()) }
    }
})
