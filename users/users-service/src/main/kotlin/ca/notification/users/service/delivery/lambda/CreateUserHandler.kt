package ca.notification.users.service.delivery.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import jakarta.inject.Singleton
import ca.notification.users.service.port.inbound.CreateUserUseCase

@Singleton
class CreateUserHandler(
    private val createUserUseCase: CreateUserUseCase
) : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context?): APIGatewayProxyResponseEvent {
        val name = input.queryStringParameters?.get("name") ?: "Unknown"
        val email = input.queryStringParameters?.get("email") ?: "unknown@example.com"
        val phoneNumber = input.queryStringParameters?.get("phoneNumber") ?: "000-000-0000"

        val user = createUserUseCase.execute(CreateUserUseCase.Command(name, email, phoneNumber))

        return APIGatewayProxyResponseEvent().apply {
            statusCode = 201
            body = "User created: ${user.id} (${user.name})"
        }
    }
}
