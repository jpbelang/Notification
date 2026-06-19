package ca.notification.users.service.delivery.lambda

import ca.notification.users.service.adapter.persistence.InMemoryUserRepository
import ca.notification.users.service.adapter.persistence.InMemoryCredentialsRepository
import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.micronaut.json.JsonMapper
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest
class CreateUserHandlerTest(
    private val handler: CreateUserHandler,
    private val jsonMapper: JsonMapper,
    private val userRepository: InMemoryUserRepository,
    private val credentialsRepository: InMemoryCredentialsRepository
) : StringSpec({

    "should create a user and return 201" {
        val body = mapOf(
            "name" to "John Doe",
            "email" to "john@example.com",
            "phoneNumber" to "555-1234",
            "password" to "secret"
        )
        val request = APIGatewayProxyRequestEvent().apply {
            this.body = jsonMapper.writeValueAsString(body)
        }

        val response = handler.handleRequest(request, null)
        val responseBody = response.body ?: ""

        response.statusCode shouldBe 201
        responseBody shouldContain "id"
        responseBody shouldContain "John Doe"
        responseBody shouldContain "john@example.com"
        responseBody shouldContain "555-1234"
        responseBody shouldNotContain "secret"
        responseBody shouldNotContain "password"

        // Further validation: parse and check fields
        val parsedResponse = jsonMapper.readValue(responseBody, Map::class.java)
        parsedResponse["name"] shouldBe "John Doe"
        parsedResponse["email"] shouldBe "john@example.com"
        parsedResponse["phoneNumber"] shouldBe "555-1234"
        parsedResponse["id"] shouldNotBe null
        parsedResponse.containsKey("password") shouldBe false

        // Verify user was saved in repository without password
        val savedUserId = TypedUUID.fromString<User>(parsedResponse["id"] as String)
        val savedUser = userRepository.findById(savedUserId)
        savedUser shouldNotBe null
        savedUser?.name shouldBe "John Doe"
        savedUser?.email shouldBe "john@example.com"
        savedUser?.phoneNumber shouldBe "555-1234"

        // Verify credentials were saved separately with password
        val savedCredentials = credentialsRepository.findByUserId(savedUserId)
        savedCredentials shouldNotBe null
        savedCredentials?.email shouldBe "john@example.com"
        savedCredentials?.password shouldBe "secret"
    }
})
