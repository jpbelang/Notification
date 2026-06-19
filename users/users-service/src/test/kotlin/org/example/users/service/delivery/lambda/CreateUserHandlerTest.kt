package org.example.users.service.delivery.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest
class CreateUserHandlerTest(
    private val handler: CreateUserHandler
) : StringSpec({

    "should create a user and return 201" {
        val request = APIGatewayProxyRequestEvent().apply {
            queryStringParameters = mapOf(
                "name" to "John Doe",
                "email" to "john@example.com"
            )
        }

        val response = handler.handleRequest(request, null)

        response.statusCode shouldBe 201
        response.body shouldContain "User created"
        response.body shouldContain "John Doe"
    }
})
