package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class InMemoryUserRepositoryTest : StringSpec({

    val repository = InMemoryUserRepository()

    "should save and find a user" {
        val userId = TypedUUID.create<User>()
        val user = User(
            id = userId,
            name = "Jane Doe",
            email = "jane@example.com",
            phoneNumber = "555-9876",
            password = "securePassword123"
        )

        repository.save(user)

        val savedUser = repository.findById(userId)
        savedUser shouldNotBe null
        savedUser?.id shouldBe userId
        savedUser?.name shouldBe "Jane Doe"
        savedUser?.email shouldBe "jane@example.com"
        savedUser?.phoneNumber shouldBe "555-9876"
    }

    "should return null if user not found" {
        val nonExistentId = TypedUUID.create<User>()
        repository.findById(nonExistentId) shouldBe null
    }

    "should overwrite existing user with same id" {
        val userId = TypedUUID.create<User>()
        val user1 = User(userId, "User One", "one@example.com", "111", "pass1")
        val user2 = User(userId, "User Two", "two@example.com", "222", "pass2")

        repository.save(user1)
        repository.save(user2)

        val savedUser = repository.findById(userId)
        savedUser?.name shouldBe "User Two"
        savedUser?.email shouldBe "two@example.com"
    }
})
