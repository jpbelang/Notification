package ca.notification.users.service.delivery.lambda

import ca.notification.users.service.port.inbound.CreateUserUseCase

class UserHandler(
    private val createUserUseCase: CreateUserUseCase
) {

    fun create(request: CreateUserRequest): CreateUserResponse {

        System.getenv().forEach { (key, value) ->
            println("$key = $value")
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
