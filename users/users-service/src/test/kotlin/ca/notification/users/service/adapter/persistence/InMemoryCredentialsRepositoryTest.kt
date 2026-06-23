package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.domain.UserExistsException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class InMemoryCredentialsRepositoryTest : StringSpec({

    lateinit var repository: InMemoryCredentialsRepository

    beforeTest {
        repository = InMemoryCredentialsRepository()
    }

    "should save and find credentials" {
        val user = User.createNew(
            name = "John Doe",
            email = "john@example.com",
            phoneNumber = "555-1234",
            password = "secretPassword"
        )

        val result = repository.save(user)

        val savedCredentials = repository.findByUserId(result)
        savedCredentials shouldNotBe null
        savedCredentials?.userId shouldBe result
        savedCredentials?.email shouldBe "john@example.com"
        savedCredentials?.password shouldBe "secretPassword"
    }

    "should throw exception if email already exists" {
        val user1 = User.createNew("User 1", "duplicate@example.com", "111", "pass")
        val user2 = User.createNew("User 2", "duplicate@example.com", "222", "pass")

        repository.save(user1)

        shouldThrow<UserExistsException> {
            repository.save(user2)
        }
    }

    "should return null if credentials not found" {
        val nonExistentId = TypedUUID.create<User>()
        repository.findByUserId(nonExistentId) shouldBe null
    }

    "should update credentials" {
        val user = User.createNew("John Doe", "john@example.com", "555", "secret")
        val userId = repository.save(user)
        
        val updatedUser = User.from(userId, "John Updated", "updated@example.com", "555")
        repository.update(updatedUser)

        val saved = repository.findByUserId(userId)
        saved?.email shouldBe "updated@example.com"
    }

    "should throw exception if updated email already exists for another user" {
        val user1 = User.createNew("User 1", "email1@example.com", "111", "pass")
        val user2 = User.createNew("User 2", "email2@example.com", "222", "pass")

        val userId1 = repository.save(user1)
        repository.save(user2)

        val updatedUser1 = User.from(userId1, "User 1", "email2@example.com", "111")

        shouldThrow<UserExistsException> {
            repository.update(updatedUser1)
        }
    }
})
