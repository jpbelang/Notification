package ca.notification.users.service.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class UserTest : StringSpec({

    "createNew should create a NewUser with correct details" {
        val name = "John Doe"
        val email = "john@example.com"
        val phoneNumber = "555-1234"
        val password = "securePassword123"

        val newUser = User.createNew(name, email, phoneNumber, password)

        newUser.name shouldBe name
        newUser.email shouldBe email
        newUser.phoneNumber shouldBe phoneNumber
        newUser.password shouldBe password
    }

    "from should create a User with correct details including id" {
        val id = TypedUUID.create<User>()
        val name = "Jane Doe"
        val email = "jane@example.com"
        val phoneNumber = "555-5678"
        val password = "anotherPassword456"

        val user = User.from(id, name, email, phoneNumber, password)

        user.id shouldBe id
        user.name shouldBe name
        user.email shouldBe email
        user.phoneNumber shouldBe phoneNumber
        user.password shouldBe password
    }

    "withId should create a User from NewUser and an id" {
        val newUser = NewUser(
            name = "Bob Smith",
            email = "bob@example.com",
            phoneNumber = "555-9999",
            password = "bobPassword789"
        )
        val id = TypedUUID.create<User>()

        val user = User.withId(newUser, id)

        user.id shouldBe id
        user.name shouldBe newUser.name
        user.email shouldBe newUser.email
        user.phoneNumber shouldBe newUser.phoneNumber
        user.password shouldBe newUser.password
    }
})
