package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.port.outbound.UserRepository
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

@Singleton
@Requires(property = "micronaut.environment", value = "lambda")
class DynamoUserRepository(
    private val dynamoDbClient: DynamoDbClient,
    @Property(name = "dynamodb.table-name") private val tableName: String
) : UserRepository {

    override fun save(user: User) {
        val item = mapOf(
            "pk" to AttributeValue.builder().s("id=${user.id}").build(),
            "sk" to AttributeValue.builder().s("user").build(),
            "gsipk" to AttributeValue.builder().s("user=${user.email}").build(),
            "gsisk" to AttributeValue.builder().s("user").build(),
            "id" to AttributeValue.builder().s(user.id.toString()).build(),
            "name" to AttributeValue.builder().s(user.name).build(),
            "email" to AttributeValue.builder().s(user.email).build(),
            "phoneNumber" to AttributeValue.builder().s(user.phoneNumber).build()
        )

        val request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build()

        dynamoDbClient.putItem(request)
    }

    override fun findById(id: TypedUUID<User>): User? {
        val request = GetItemRequest.builder()
            .tableName(tableName)
            .key(
                mapOf(
                    "pk" to AttributeValue.builder().s("id=$id").build(),
                    "sk" to AttributeValue.builder().s("user").build()
                )
            )
            .build()

        val response = dynamoDbClient.getItem(request)
        return if (response.hasItem()) response.item().toDomain() else null
    }

    override fun findByEmail(email: String): User? {
        val request = QueryRequest.builder()
            .tableName(tableName)
            .indexName("gsipk-gsisk-index")
            .keyConditionExpression("gsipk = :pk AND gsisk = :sk")
            .expressionAttributeValues(
                mapOf(
                    ":pk" to AttributeValue.builder().s("user=$email").build(),
                    ":sk" to AttributeValue.builder().s("user").build()
                )
            )
            .limit(1)
            .build()

        val response = dynamoDbClient.query(request)
        return response.items().firstOrNull()?.toDomain()
    }

    override fun delete(user: User) {
        val request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(
                mapOf(
                    "pk" to AttributeValue.builder().s("id=${user.id}").build(),
                    "sk" to AttributeValue.builder().s("user").build()
                )
            )
            .build()
        dynamoDbClient.deleteItem(request)
    }

    private fun Map<String, AttributeValue>.toDomain(): User {
        return User.from(
            id = TypedUUID.fromString(this["id"]!!.s()),
            name = this["name"]!!.s(),
            email = this["email"]!!.s(),
            phoneNumber = this["phoneNumber"]!!.s()
        )
    }
}
