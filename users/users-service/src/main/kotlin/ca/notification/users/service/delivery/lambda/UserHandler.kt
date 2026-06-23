package ca.notification.users.service.delivery.lambda

import ca.notification.users.service.port.inbound.CreateUserUseCase
import org.slf4j.LoggerFactory

class UserHandler(
    private val createUserUseCase: CreateUserUseCase
) {

    private val logger = LoggerFactory.getLogger(UserHandler::class.java)

    fun create(request: CreateUserRequest): CreateUserResponse {

        System.getenv().forEach { (key, value) ->
            logger.info("$key = $value")
        }

        val user = createUserUseCase.execute(
            CreateUserUseCase.Command(
                request.name,
                request.email,
                request.phoneNumber,
                request.password
            )
        )

        return CreateUserResponse(
            id = user.id.toString(),
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber
        )
    }
}
