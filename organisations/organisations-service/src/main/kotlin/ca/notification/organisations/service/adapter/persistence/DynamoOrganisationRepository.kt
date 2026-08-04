package ca.notification.organisations.service.adapter.persistence

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.Participant
import ca.notification.organisations.service.domain.Role
import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.util.UUID

@Singleton
@Requires(property = "micronaut.environment", value = "lambda")
class DynamoOrganisationRepository(
    private val dynamoDbClient: DynamoDbClient,
    @Property(name = "dynamodb.table-name") private val tableName: String
) : OrganisationRepository {

    override fun save(organisation: Organisation) {
        val request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapOf(
                "pk" to AttributeValue.builder().s("organisationId=${organisation.id}").build(),
                "sk" to AttributeValue.builder().s("organisation").build(),
                "id" to AttributeValue.builder().s(organisation.id.toString()).build(),
                "name" to AttributeValue.builder().s(organisation.name).build()
            ))
            .build()
        dynamoDbClient.putItem(request)
    }

    override fun addParticipant(organisationId: TypedUUID<Organisation>, participant: Participant) {
        val request = PutItemRequest.builder()
            .tableName(tableName)
            .item(mapOf(
                "pk" to AttributeValue.builder().s("organisationId=$organisationId").build(),
                "sk" to AttributeValue.builder().s("participant#${participant.id}").build(),
                "id" to AttributeValue.builder().s(participant.id.toString()).build(),
                "role" to AttributeValue.builder().s(participant.role.name).build()
            ))
            .build()
        dynamoDbClient.putItem(request)
    }

    override fun removeParticipant(organisationId: TypedUUID<Organisation>, participantId: UUID) {
        val request = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(mapOf(
                "pk" to AttributeValue.builder().s("organisationId=$organisationId").build(),
                "sk" to AttributeValue.builder().s("participant#$participantId").build()
            ))
            .build()
        dynamoDbClient.deleteItem(request)
    }

    override fun findById(id: TypedUUID<Organisation>): Organisation? {
        val items = queryAllForOrg(id)
        return if (items.isNotEmpty()) items.toOrganisation() else null
    }

    override fun findAll(): List<Organisation> {
        val request = ScanRequest.builder()
            .tableName(tableName)
            .build()
        val response = dynamoDbClient.scan(request)
        return response.items()
            .groupBy { it["pk"]!!.s() }
            .values
            .map { it.toOrganisation() }
    }

    override fun delete(organisation: Organisation) {
        val items = queryAllForOrg(organisation.id)
        if (items.isEmpty()) return

        val actions = items.map { item ->
            TransactWriteItem.builder().delete(
                Delete.builder()
                    .tableName(tableName)
                    .key(mapOf(
                        "pk" to item["pk"]!!,
                        "sk" to item["sk"]!!
                    ))
                    .build()
            ).build()
        }

        dynamoDbClient.transactWriteItems(TransactWriteItemsRequest.builder().transactItems(actions).build())
    }

    private fun queryAllForOrg(id: TypedUUID<Organisation>): List<Map<String, AttributeValue>> {
        val request = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression("pk = :pk")
            .expressionAttributeValues(mapOf(":pk" to AttributeValue.builder().s("organisationId=$id").build()))
            .build()
        return dynamoDbClient.query(request).items()
    }

    private fun List<Map<String, AttributeValue>>.toOrganisation(): Organisation {
        val orgItem = find { it["sk"]?.s() == "organisation" } ?: throw IllegalStateException("Organisation item not found")

        return Organisation.from(
            id = TypedUUID.fromString(orgItem["id"]!!.s()),
            name = orgItem["name"]!!.s()
        )
    }
}
