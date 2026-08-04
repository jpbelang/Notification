package ca.notification.organisations.service.micronaut

import ca.notification.organisations.service.adapter.persistence.InMemoryOrganisationRepository
import ca.notification.organisations.service.adapter.messaging.InMemoryNotificationPublisher
import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.TypedUUID
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain
import io.micronaut.context.ApplicationContext
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import io.micronaut.json.JsonMapper
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest
class OrganisationLambdaTest(
    private val applicationContext: ApplicationContext,
    private val jsonMapper: JsonMapper,
    private val organisationRepository: InMemoryOrganisationRepository,
    private val notificationPublisher: InMemoryNotificationPublisher
) : StringSpec({

    val handler = ApiGatewayProxyRequestEventFunction(applicationContext)

    beforeTest {
        notificationPublisher.clear()
    }

    "should create an organisation, return 201 and publish event" {
        val body = mapOf("name" to "Test Org")
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "POST"
            this.path = "/organisations"
            this.body = jsonMapper.writeValueAsString(body)
        }

        val response = handler.handleRequest(request, null)
        val responseBody = response.body ?: ""

        response.statusCode shouldBe 201
        responseBody shouldContain "id"
        responseBody shouldContain "Test Org"

        val parsedResponse = jsonMapper.readValue(responseBody, Map::class.java)
        val id = parsedResponse["id"] as String
        
        val saved = organisationRepository.findById(TypedUUID.fromString(id))
        saved shouldNotBe null
        saved?.name shouldBe "Test Org"

        val events = notificationPublisher.getEvents()
        events shouldHaveSize 1
        events[0].type shouldBe "NewOrganisation"
        (events[0].payload as Organisation).name shouldBe "Test Org"
    }

    "should get organisation by id" {
        val org = Organisation.from(TypedUUID.create(), "Finder Org")
        organisationRepository.save(org)

        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "GET"
            this.path = "/organisations/${org.id}"
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 200
        response.body shouldContain "Finder Org"
    }

    "should find all organisations" {
        organisationRepository.save(Organisation.from(TypedUUID.create(), "Org 1"))
        organisationRepository.save(Organisation.from(TypedUUID.create(), "Org 2"))

        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "GET"
            this.path = "/organisations"
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 200
        response.body shouldContain "Org 1"
        response.body shouldContain "Org 2"
    }

    "should update organisation and publish event" {
        val org = Organisation.from(TypedUUID.create(), "Old Name")
        organisationRepository.save(org)
        notificationPublisher.clear()

        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "PUT"
            this.path = "/organisations/${org.id}"
            this.body = """{"name": "New Name"}"""
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 200
        response.body shouldContain "New Name"

        organisationRepository.findById(org.id)?.name shouldBe "New Name"

        val events = notificationPublisher.getEvents()
        events shouldHaveSize 1
        events[0].type shouldBe "UpdatedOrganisation"
        (events[0].payload as Organisation).name shouldBe "New Name"
    }

    "should delete organisation and publish event" {
        val org = Organisation.from(TypedUUID.create(), "Delete Me")
        organisationRepository.save(org)
        notificationPublisher.clear()

        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "DELETE"
            this.path = "/organisations/${org.id}"
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 204

        organisationRepository.findById(org.id) shouldBe null

        val events = notificationPublisher.getEvents()
        events shouldHaveSize 1
        events[0].type shouldBe "DeletedOrganisation"
        (events[0].payload as Organisation).id shouldBe org.id
    }

    "should return 404 for non-existent organisation" {
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "GET"
            this.path = "/organisations/${TypedUUID.create<Organisation>()}"
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 404
    }

    "should add a participant and publish event" {
        val org = Organisation.from(TypedUUID.create(), "Participant Org")
        organisationRepository.save(org)
        notificationPublisher.clear()

        val participantId = java.util.UUID.randomUUID().toString()
        val body = mapOf("participantId" to participantId, "role" to "admin")
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "POST"
            this.path = "/organisations/${org.id}/participants"
            this.body = jsonMapper.writeValueAsString(body)
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 200
        response.body shouldContain participantId
        response.body shouldContain "admin"

        val updatedOrg = organisationRepository.findById(org.id)
        updatedOrg?.participants?.size shouldBe 1
        updatedOrg?.participants?.get(0)?.id.toString() shouldBe participantId

        val events = notificationPublisher.getEvents()
        events shouldHaveSize 1
        events[0].type shouldBe "UpdatedOrganisation"
        (events[0].payload as Organisation).id shouldBe org.id
    }

    "should remove a participant and publish event" {
        val participantId = java.util.UUID.randomUUID()
        val org = Organisation.from(
            TypedUUID.create(),
            "Removal Org",
            listOf(ca.notification.organisations.service.domain.Participant(participantId, ca.notification.organisations.service.domain.Role.MEMBER))
        )
        organisationRepository.save(org)
        notificationPublisher.clear()

        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "DELETE"
            this.path = "/organisations/${org.id}/participants/$participantId"
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 200

        val updatedOrg = organisationRepository.findById(org.id)
        updatedOrg?.participants?.size shouldBe 0

        val events = notificationPublisher.getEvents()
        events shouldHaveSize 1
        events[0].type shouldBe "UpdatedOrganisation"
        (events[0].payload as Organisation).id shouldBe org.id
    }
})
