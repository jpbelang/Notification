package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse

class DynamoUserRepositoryTest : StringSpec({

    val dynamoDbClient = mockk<DynamoDbClient>()
    val tableName = "UsersTable"
    val repository = DynamoUserRepository(dynamoDbClient, tableName)

    "should save user to dynamodb" {
        val user = User.from(
            id = TypedUUID.create(),
            name = "John Doe",
            email = "john@example.com",
            phoneNumber = "555-1234"
        )

        every { dynamoDbClient.putItem(any<PutItemRequest>()) } returns PutItemResponse.builder().build()

        repository.save(user)

        verify {
            dynamoDbClient.putItem(withArg<PutItemRequest> {
                it.tableName() shouldBe tableName
                it.item()["pk"]?.s() shouldBe "user=${user.email}"
                it.item()["sk"]?.s() shouldBe "user"
                it.item()["id"]?.s() shouldBe user.id.toString()
                it.item()["name"]?.s() shouldBe user.name
                it.item()["email"]?.s() shouldBe user.email
                it.item()["phoneNumber"]?.s() shouldBe user.phoneNumber
            })
        }
    }
})
