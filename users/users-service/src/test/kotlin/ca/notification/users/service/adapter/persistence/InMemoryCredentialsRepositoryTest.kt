package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class InMemoryCredentialsRepositoryTest : StringSpec({

    val repository = InMemoryCredentialsRepository()

    "should save and find credentials" {
        val userId = TypedUUID.create<User>()
        val user = User(
            id = userId,
            name = "John Doe",
            email = "john@example.com",
            phoneNumber = "555-1234",
            password = "secretPassword"
        )

        repository.save(user)

        val savedCredentials = repository.findByUserId(userId)
        savedCredentials shouldNotBe null
        savedCredentials?.userId shouldBe userId
        savedCredentials?.email shouldBe "john@example.com"
        savedCredentials?.password shouldBe "secretPassword"
    }

    "should return null if credentials not found" {
        val nonExistentId = TypedUUID.create<User>()
        repository.findByUserId(nonExistentId) shouldBe null
    }
})
