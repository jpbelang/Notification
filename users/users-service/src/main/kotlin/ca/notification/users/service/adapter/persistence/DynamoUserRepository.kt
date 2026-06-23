package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.User
import ca.notification.users.service.domain.UserExistsException
import ca.notification.users.service.port.outbound.UserRepository
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest

@Singleton
@Requires(property = "micronaut.environment", value = "lambda")
class DynamoUserRepository(
    private val dynamoDbClient: DynamoDbClient,
    @Property(name = "dynamodb.table-name") private val tableName: String
) : UserRepository {

    override fun save(user: User) {
        val item = mapOf(
            "pk" to AttributeValue.builder().s("user=${user.email}").build(),
            "sk" to AttributeValue.builder().s("user").build(),
            "gsipk" to AttributeValue.builder().s("user=${user.id}").build(),
            "gsisk" to AttributeValue.builder().s("user").build(),
            "id" to AttributeValue.builder().s(user.id.toString()).build(),
            "name" to AttributeValue.builder().s(user.name).build(),
            "email" to AttributeValue.builder().s(user.email).build(),
            "phoneNumber" to AttributeValue.builder().s(user.phoneNumber).build()
        )

        val request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .conditionExpression("attribute_not_exists(pk) OR id = :id")
            .expressionAttributeValues(mapOf(":id" to AttributeValue.builder().s(user.id.toString()).build()))
            .build()

        try {
            dynamoDbClient.putItem(request)
        } catch (e: ConditionalCheckFailedException) {
            throw UserExistsException("User with email ${user.email} already exists")
        }
    }
}
