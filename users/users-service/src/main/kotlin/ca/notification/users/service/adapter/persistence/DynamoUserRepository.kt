package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.User
import ca.notification.users.service.port.outbound.UserRepository
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest

@Singleton
@Requires(property = "persistence.type", value = "dynamodb")
class DynamoUserRepository(
    private val dynamoDbClient: DynamoDbClient,
    @Property(name = "persistence.dynamodb.table-name") private val tableName: String
) : UserRepository {

    override fun save(user: User) {
        val item = mapOf(
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
}
