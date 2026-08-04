package ca.notification.organisations.service.adapter.persistence

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.Participant
import ca.notification.organisations.service.domain.Role
import ca.notification.organisations.service.domain.TypedUUID
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.util.UUID

class DynamoOrganisationRepositoryTest : StringSpec({

    val dynamoDbClient = mockk<DynamoDbClient>()
    val tableName = "OrganisationsTable"
    val repository = DynamoOrganisationRepository(dynamoDbClient, tableName)

    "should save organisation item only" {
        val orgId = TypedUUID.create<Organisation>()
        val organisation = Organisation.from(
            id = orgId,
            name = "Test Org"
        )

        every { dynamoDbClient.putItem(any<PutItemRequest>()) } returns PutItemResponse.builder().build()

        repository.save(organisation)

        verify {
            dynamoDbClient.putItem(withArg<PutItemRequest> {
                it.tableName() shouldBe tableName
                it.item()["pk"]?.s() shouldBe "organisationId=$orgId"
                it.item()["sk"]?.s() shouldBe "organisation"
                it.item()["name"]?.s() shouldBe "Test Org"
            })
        }
    }

    "should add participant item" {
        val orgId = TypedUUID.create<Organisation>()
        val partId = UUID.randomUUID()
        val participant = Participant(partId, Role.ADMIN)

        every { dynamoDbClient.putItem(any<PutItemRequest>()) } returns PutItemResponse.builder().build()

        repository.addParticipant(orgId, participant)

        verify {
            dynamoDbClient.putItem(withArg<PutItemRequest> {
                it.tableName() shouldBe tableName
                it.item()["pk"]?.s() shouldBe "organisationId=$orgId"
                it.item()["sk"]?.s() shouldBe "participant#$partId"
                it.item()["role"]?.s() shouldBe "ADMIN"
            })
        }
    }

    "should remove participant item" {
        val orgId = TypedUUID.create<Organisation>()
        val partId = UUID.randomUUID()

        every { dynamoDbClient.deleteItem(any<DeleteItemRequest>()) } returns DeleteItemResponse.builder().build()

        repository.removeParticipant(orgId, partId)

        verify {
            dynamoDbClient.deleteItem(withArg<DeleteItemRequest> {
                it.tableName() shouldBe tableName
                it.key()["pk"]?.s() shouldBe "organisationId=$orgId"
                it.key()["sk"]?.s() shouldBe "participant#$partId"
            })
        }
    }

    "should find organisation by id and not join participants" {
        val orgId = TypedUUID.create<Organisation>()
        
        val item = mapOf(
            "pk" to AttributeValue.builder().s("organisationId=$orgId").build(),
            "sk" to AttributeValue.builder().s("organisation").build(),
            "id" to AttributeValue.builder().s(orgId.toString()).build(),
            "name" to AttributeValue.builder().s("Test Org").build()
        )

        every { dynamoDbClient.getItem(any<GetItemRequest>()) } returns GetItemResponse.builder().item(item).build()

        val organisation = repository.findById(orgId)

        organisation shouldNotBe null
        organisation?.name shouldBe "Test Org"
        
        verify {
            dynamoDbClient.getItem(withArg<GetItemRequest> {
                it.key()["pk"]?.s() shouldBe "organisationId=$orgId"
                it.key()["sk"]?.s() shouldBe "organisation"
            })
        }
    }

    "should delete organisation and all its items" {
        val orgId = TypedUUID.create<Organisation>()
        val partId = UUID.randomUUID()
        val organisation = Organisation.from(orgId, "Delete Me")

        val items = listOf(
            mapOf(
                "pk" to AttributeValue.builder().s("organisationId=$orgId").build(),
                "sk" to AttributeValue.builder().s("organisation").build()
            ),
            mapOf(
                "pk" to AttributeValue.builder().s("organisationId=$orgId").build(),
                "sk" to AttributeValue.builder().s("participant#$partId").build()
            )
        )

        every { dynamoDbClient.query(any<QueryRequest>()) } returns QueryResponse.builder().items(items).build()
        every { dynamoDbClient.transactWriteItems(any<TransactWriteItemsRequest>()) } returns TransactWriteItemsResponse.builder().build()

        repository.delete(organisation)

        verify {
            dynamoDbClient.transactWriteItems(withArg<TransactWriteItemsRequest> {
                it.transactItems() shouldHaveSize 2
                it.transactItems()[0].delete().key()["pk"]?.s() shouldBe "organisationId=$orgId"
                it.transactItems()[0].delete().key()["sk"]?.s() shouldBe "organisation"
                it.transactItems()[1].delete().key()["pk"]?.s() shouldBe "organisationId=$orgId"
                it.transactItems()[1].delete().key()["sk"]?.s() shouldBe "participant#$partId"
            })
        }
    }

    "should find all organisations" {
        val orgId1 = TypedUUID.create<Organisation>()
        val orgId2 = TypedUUID.create<Organisation>()

        val items = listOf(
            mapOf(
                "pk" to AttributeValue.builder().s("organisationId=$orgId1").build(),
                "sk" to AttributeValue.builder().s("organisation").build(),
                "id" to AttributeValue.builder().s(orgId1.toString()).build(),
                "name" to AttributeValue.builder().s("Org 1").build()
            ),
            mapOf(
                "pk" to AttributeValue.builder().s("organisationId=$orgId2").build(),
                "sk" to AttributeValue.builder().s("organisation").build(),
                "id" to AttributeValue.builder().s(orgId2.toString()).build(),
                "name" to AttributeValue.builder().s("Org 2").build()
            )
        )

        every { dynamoDbClient.scan(any<ScanRequest>()) } returns ScanResponse.builder().items(items).build()

        val result = repository.findAll()

        result shouldHaveSize 2
        result.map { it.name } shouldBe listOf("Org 1", "Org 2")
    }

    "findAll should ignore non-organisation items and not throw exception" {
        val orgId = TypedUUID.create<Organisation>()
        val items = listOf(
            mapOf(
                "pk" to AttributeValue.builder().s("organisationId=$orgId").build(),
                "sk" to AttributeValue.builder().s("organisation").build(),
                "id" to AttributeValue.builder().s(orgId.toString()).build(),
                "name" to AttributeValue.builder().s("Test Org").build()
            ),
            mapOf(
                "pk" to AttributeValue.builder().s("organisationId=$orgId").build(),
                "sk" to AttributeValue.builder().s("participant#some-id").build()
            ),
            mapOf(
                "pk" to AttributeValue.builder().s("something-else").build(),
                "sk" to AttributeValue.builder().s("other").build()
            )
        )

        every { dynamoDbClient.scan(any<ScanRequest>()) } returns ScanResponse.builder().items(items).build()

        val result = repository.findAll()
        result shouldHaveSize 1
        result[0].name shouldBe "Test Org"
    }
})
