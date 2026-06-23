package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException

class InMemoryCredentialsRepositoryTest : StringSpec({

    val repository = InMemoryCredentialsRepository()

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

        shouldThrow<UsernameExistsException> {
            repository.save(user2)
        }
    }

    "should return null if credentials not found" {
        val nonExistentId = TypedUUID.create<User>()
        repository.findByUserId(nonExistentId) shouldBe null
    }
})
