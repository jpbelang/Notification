package ca.notification.users.service.delivery.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import io.micronaut.json.JsonMapper
import jakarta.inject.Singleton
import ca.notification.users.service.port.inbound.CreateUserUseCase

@Singleton
class CreateUserHandler(
    private val createUserUseCase: CreateUserUseCase,
    private val jsonMapper: JsonMapper
) : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(input: APIGatewayProxyRequestEvent, context: Context?): APIGatewayProxyResponseEvent {
        val request = input.body?.let {
            jsonMapper.readValue(it, CreateUserRequest::class.java)
        } ?: throw IllegalArgumentException("Missing request body")

        val user = createUserUseCase.execute(
            CreateUserUseCase.Command(
                request.name,
                request.email,
                request.phoneNumber,
                request.password
            )
        )

        val responseBody = CreateUserResponse(
            id = user.id.toString(),
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber
        )

        return APIGatewayProxyResponseEvent().apply {
            statusCode = 201
            body = jsonMapper.writeValueAsString(responseBody)
        }
    }
}
