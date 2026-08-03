package ca.notification.organisations.service.micronaut

import ca.notification.organisations.service.adapter.persistence.InMemoryOrganisationRepository
import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.TypedUUID
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.micronaut.context.ApplicationContext
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import io.micronaut.json.JsonMapper
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest
class OrganisationLambdaTest(
    private val applicationContext: ApplicationContext,
    private val jsonMapper: JsonMapper,
    private val organisationRepository: InMemoryOrganisationRepository
) : StringSpec({

    val handler = ApiGatewayProxyRequestEventFunction(applicationContext)

    "should create an organisation and return 201" {
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

    "should update organisation" {
        val org = Organisation.from(TypedUUID.create(), "Old Name")
        organisationRepository.save(org)

        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "PUT"
            this.path = "/organisations/${org.id}"
            this.body = """{"name": "New Name"}"""
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 200
        response.body shouldContain "New Name"

        organisationRepository.findById(org.id)?.name shouldBe "New Name"
    }

    "should delete organisation" {
        val org = Organisation.from(TypedUUID.create(), "Delete Me")
        organisationRepository.save(org)

        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "DELETE"
            this.path = "/organisations/${org.id}"
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 204

        organisationRepository.findById(org.id) shouldBe null
    }

    "should return 404 for non-existent organisation" {
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "GET"
            this.path = "/organisations/${TypedUUID.create<Organisation>()}"
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 404
    }
})
