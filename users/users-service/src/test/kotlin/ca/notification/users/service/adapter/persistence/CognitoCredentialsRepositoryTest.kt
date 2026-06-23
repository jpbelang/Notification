package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType
import java.util.*

class CognitoCredentialsRepositoryTest : StringSpec({

    val cognitoClient = mockk<CognitoIdentityProviderClient>()
    val userPoolId = "us-east-1_abc123"
    val repository = CognitoCredentialsRepository(cognitoClient, userPoolId)

    "should save credentials to cognito and return sub as user id" {
        val user = User(
            id = TypedUUID.create(),
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
                attributes["custom:userId"] shouldBe user.id.toString()
                it.messageActionAsString() shouldBe "SUPPRESS"
            })
        }
    }
})
