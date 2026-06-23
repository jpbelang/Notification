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
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

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
                it.item()["gsipk"]?.s() shouldBe "user=${user.id}"
                it.item()["gsisk"]?.s() shouldBe "user"
                it.item()["id"]?.s() shouldBe user.id.toString()
                it.item()["name"]?.s() shouldBe user.name
                it.item()["email"]?.s() shouldBe user.email
                it.item()["phoneNumber"]?.s() shouldBe user.phoneNumber
                it.conditionExpression() shouldBe "attribute_not_exists(pk) OR id = :id"
                it.expressionAttributeValues()[":id"]?.s() shouldBe user.id.toString()
            })
        }
    }

    "should throw UserExistsException if conditional check fails in dynamodb" {
        val user = User.from(TypedUUID.create(), "Duplicate", "dup@example.com", "000")

        every { dynamoDbClient.putItem(any<PutItemRequest>()) } throws ConditionalCheckFailedException.builder().message("Conflict").build()

        shouldThrow<UserExistsException> {
            repository.save(user)
        }
    }

    "should find user by id" {
        val userId = TypedUUID.create<User>()
        val item = mapOf(
            "id" to AttributeValue.builder().s(userId.toString()).build(),
            "name" to AttributeValue.builder().s("John Doe").build(),
            "email" to AttributeValue.builder().s("john@example.com").build(),
            "phoneNumber" to AttributeValue.builder().s("555-1234").build()
        )

        every { dynamoDbClient.query(any<QueryRequest>()) } returns QueryResponse.builder().items(item).build()

        val foundUser = repository.findById(userId)

        foundUser?.id shouldBe userId
        foundUser?.name shouldBe "John Doe"

        verify {
            dynamoDbClient.query(withArg<QueryRequest> {
                it.tableName() shouldBe tableName
                it.indexName() shouldBe "gsipk-gsisk-index"
                it.keyConditionExpression() shouldBe "gsipk = :pk AND gsisk = :sk"
                it.expressionAttributeValues()[":pk"]?.s() shouldBe "user=$userId"
                it.expressionAttributeValues()[":sk"]?.s() shouldBe "user"
            })
        }
    }

    "should find user by email" {
        val userId = TypedUUID.create<User>()
        val item = mapOf(
            "id" to AttributeValue.builder().s(userId.toString()).build(),
            "name" to AttributeValue.builder().s("John Doe").build(),
            "email" to AttributeValue.builder().s("john@example.com").build(),
            "phoneNumber" to AttributeValue.builder().s("555-1234").build()
        )

        every { dynamoDbClient.getItem(any<GetItemRequest>()) } returns GetItemResponse.builder().item(item).build()

        val foundUser = repository.findByEmail("john@example.com")

        foundUser?.email shouldBe "john@example.com"

        verify {
            dynamoDbClient.getItem(withArg<GetItemRequest> {
                it.tableName() shouldBe tableName
                it.key()["pk"]?.s() shouldBe "user=john@example.com"
                it.key()["sk"]?.s() shouldBe "user"
            })
        }
    }
})
