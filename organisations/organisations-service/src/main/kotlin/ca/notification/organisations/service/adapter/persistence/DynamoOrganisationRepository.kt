package ca.notification.organisations.service.adapter.persistence

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

@Singleton
@Requires(property = "micronaut.environment", value = "lambda")
class DynamoOrganisationRepository(
    private val dynamoDbClient: DynamoDbClient,
    @Property(name = "dynamodb.table-name") private val tableName: String
) : OrganisationRepository {

    override fun save(organisation: Organisation) {
        val item = mapOf(
            "pk" to AttributeValue.builder().s("id=${organisation.id}").build(),
            "sk" to AttributeValue.builder().s("organisation").build(),
            "id" to AttributeValue.builder().s(organisation.id.toString()).build(),
            "name" to AttributeValue.builder().s(organisation.name).build()
        )

        val request = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build()

        dynamoDbClient.putItem(request)
    }

    override fun findById(id: TypedUUID<Organisation>): Organisation? {
        val request = GetItemRequest.builder()
            .tableName(tableName)
            .key(
                mapOf(
                    "pk" to AttributeValue.builder().s("id=$id").build(),
                    "sk" to AttributeValue.builder().s("organisation").build()
                )
            )
            .build()

        val response = dynamoDbClient.getItem(request)
        return if (response.hasItem()) response.item().toDomain() else null
    }

    override fun findAll(): List<Organisation> {
        val request = ScanRequest.builder()
            .tableName(tableName)
            .filterExpression("sk = :sk")
            .expressionAttributeValues(mapOf(":sk" to AttributeValue.builder().s("organisation").build()))
            .build()
        val response = dynamoDbClient.scan(request)
        return response.items().map { it.toDomain() }
    }

    override fun delete(organisation: Organisation) {
        val request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(
                mapOf(
                    "pk" to AttributeValue.builder().s("id=${organisation.id}").build(),
                    "sk" to AttributeValue.builder().s("organisation").build()
                )
            )
            .build()
        dynamoDbClient.deleteItem(request)
    }

    private fun Map<String, AttributeValue>.toDomain(): Organisation {
        return Organisation.from(
            id = TypedUUID.fromString(this["id"]!!.s()),
            name = this["name"]!!.s()
        )
    }
}
