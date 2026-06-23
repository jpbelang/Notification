package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.domain.UserExistsException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class InMemoryUserRepositoryTest : StringSpec({

    val repository = InMemoryUserRepository()

    "should save and find a user" {
        val userId = TypedUUID.create<User>()
        val user = User.from(
            id = userId,
            name = "Jane Doe",
            email = "jane@example.com",
            phoneNumber = "555-9876"
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
        val user1 = User.from(userId, "User One", "one@example.com", "111")
        val user2 = User.from(userId, "User Two", "two@example.com", "222")

        repository.save(user1)
        repository.save(user2)

        val savedUser = repository.findById(userId)
        savedUser?.name shouldBe "User Two"
        savedUser?.email shouldBe "two@example.com"
    }

    "should find user by id" {
        val user = User.from(TypedUUID.create(), "User ID", "id@example.com", "123")
        repository.save(user)

        val foundUser = repository.findById(user.id)
        foundUser shouldBe user
    }

    "should find user by email" {
        val user = User.from(TypedUUID.create(), "User Email", "email-find@example.com", "123")
        repository.save(user)

        val foundUser = repository.findByEmail("email-find@example.com")
        foundUser shouldBe user
    }

    "should return null if user not found" {
        repository.findById(TypedUUID.create()) shouldBe null
        repository.findByEmail("nonexistent@example.com") shouldBe null
    }

    "should throw exception if email already exists for different user" {
        val user1 = User.from(TypedUUID.create(), "User 1", "duplicate@example.com", "111")
        val user2 = User.from(TypedUUID.create(), "User 2", "duplicate@example.com", "222")

        repository.save(user1)

        shouldThrow<UserExistsException> {
            repository.save(user2)
        }
    }
})
