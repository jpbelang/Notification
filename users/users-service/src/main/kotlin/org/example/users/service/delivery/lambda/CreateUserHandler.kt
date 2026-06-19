package org.example.users.service.delivery.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import io.micronaut.core.annotation.Introspected
import io.micronaut.function.aws.MicronautRequestHandler
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.example.users.service.port.inbound.CreateUserUseCase

@Introspected
class CreateUserHandler : MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>() {

    @Inject
    lateinit var createUserUseCase: CreateUserUseCase

    override fun execute(input: APIGatewayProxyRequestEvent): APIGatewayProxyResponseEvent {
        val name = input.queryStringParameters?.get("name") ?: "Unknown"
        val email = input.queryStringParameters?.get("email") ?: "unknown@example.com"

        val user = createUserUseCase.execute(CreateUserUseCase.Command(name, email))

        return APIGatewayProxyResponseEvent().apply {
            statusCode = 201
            body = "User created: ${user.id} (${user.name})"
        }
    }
}
