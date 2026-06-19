package ca.notification.users.service.delivery.lambda

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
    private val jsonMapper: JsonMapper
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
    }
})
