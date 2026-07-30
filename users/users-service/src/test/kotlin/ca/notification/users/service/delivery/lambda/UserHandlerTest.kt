package ca.notification.users.service.delivery.lambda

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.port.inbound.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class UserHandlerTest : StringSpec({

    val createUserUseCase = mockk<CreateUserUseCase>()
    val findUserUseCase = mockk<FindUserUseCase>()
    val updateUserUseCase = mockk<UpdateUserUseCase>()
    val deleteUserUseCase = mockk<DeleteUserUseCase>()
    val authenticateUserUseCase = mockk<AuthenticateUserUseCase>()

    val handler = UserHandler(
        createUserUseCase,
        findUserUseCase,
        updateUserUseCase,
        deleteUserUseCase,
        authenticateUserUseCase
    )

    beforeTest {
        clearAllMocks()
    }

    "should authenticate user" {
        val email = "test@example.com"
        val password = "password"
        val user = User.from(TypedUUID.create(), "Test User", email, "123")
        val request = AuthenticateUserRequest(password)

        every { authenticateUserUseCase.execute(any()) } returns user

        val response = handler.authenticate(email, request)

        response shouldNotBe null
        response?.email shouldBe email
        response?.name shouldBe "Test User"
        response?.id shouldBe user.id.toString()

        verify {
            authenticateUserUseCase.execute(
                AuthenticateUserUseCase.Command(email, password)
            )
        }
    }

    "should return null when authentication fails" {
        val email = "test@example.com"
        val password = "wrong-password"
        val request = AuthenticateUserRequest(password)

        every { authenticateUserUseCase.execute(any()) } returns null

        val response = handler.authenticate(email, request)

        response shouldBe null

        verify {
            authenticateUserUseCase.execute(
                AuthenticateUserUseCase.Command(email, password)
            )
        }
    }

    "should get user by email" {
        val email = "test@example.com"
        val user = User.from(TypedUUID.create(), "Test User", email, "123")

        every { findUserUseCase.findByEmail(email) } returns user

        val response = handler.getByEmail(email)

        response shouldNotBe null
        response?.email shouldBe email
        
        verify { findUserUseCase.findByEmail(email) }
    }
})
