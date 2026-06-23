package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.domain.UserExistsException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException
import java.util.*

class CognitoCredentialsRepositoryTest : StringSpec({

    val cognitoClient = mockk<CognitoIdentityProviderClient>()
    val userPoolId = "us-east-1_abc123"
    val repository = CognitoCredentialsRepository(cognitoClient, userPoolId)

    "should save credentials to cognito and return sub as user id" {
        val user = User.createNew(
            name = "John Doe",
            email = "john@example.com",
            phoneNumber = "555-1234",
            password = "secretPassword123"
        )
        val sub = UUID.randomUUID().toString()

        every { cognitoClient.adminCreateUser(any<AdminCreateUserRequest>()) } returns AdminCreateUserResponse.builder()
            .user(UserType.builder()
                .attributes(AttributeType.builder().name("sub").value(sub).build())
                .build())
            .build()

        val result = repository.save(user)

        result.asString() shouldBe sub

        verify {
            cognitoClient.adminCreateUser(withArg<AdminCreateUserRequest> {
                it.userPoolId() shouldBe userPoolId
                it.username() shouldBe user.email
                it.temporaryPassword() shouldBe user.password
                val attributes = it.userAttributes().associate { attr -> attr.name() to attr.value() }
                attributes["email"] shouldBe user.email
                attributes["email_verified"] shouldBe "true"
                attributes["name"] shouldBe user.name
                attributes["phone_number"] shouldBe user.phoneNumber
                it.messageActionAsString() shouldBe "SUPPRESS"
            })
        }
    }

    "should throw UserExistsException if username already exists in cognito" {
        val user = User.createNew("Duplicate", "dup@example.com", "000", "pass")

        every { cognitoClient.adminCreateUser(any<AdminCreateUserRequest>()) } throws UsernameExistsException.builder().message("User already exists").build()

        shouldThrow<UserExistsException> {
            repository.save(user)
        }
    }

    "should update credentials in cognito" {
        val userId = TypedUUID.create<User>()
        val user = User.from(userId, "John Updated", "updated@example.com", "555-4321")

        every { cognitoClient.adminUpdateUserAttributes(any<AdminUpdateUserAttributesRequest>()) } returns AdminUpdateUserAttributesResponse.builder().build()

        repository.update(user)

        verify {
            cognitoClient.adminUpdateUserAttributes(withArg<AdminUpdateUserAttributesRequest> {
                it.userPoolId() shouldBe userPoolId
                it.username() shouldBe userId.toString()
                val attributes = it.userAttributes().associate { attr -> attr.name() to attr.value() }
                attributes["email"] shouldBe user.email
                attributes["email_verified"] shouldBe "true"
                attributes["name"] shouldBe user.name
                attributes["phone_number"] shouldBe user.phoneNumber
            })
        }
    }

    "should throw UserExistsException if update fails due to duplicate email in cognito" {
        val userId = TypedUUID.create<User>()
        val user = User.from(userId, "John Updated", "duplicate@example.com", "555-4321")

        every { cognitoClient.adminUpdateUserAttributes(any<AdminUpdateUserAttributesRequest>()) } throws UsernameExistsException.builder().message("User already exists").build()

        shouldThrow<UserExistsException> {
            repository.update(user)
        }
    }
})
