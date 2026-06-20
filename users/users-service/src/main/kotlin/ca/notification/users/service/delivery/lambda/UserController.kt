package ca.notification.users.service.delivery.lambda

import ca.notification.users.service.port.inbound.CreateUserUseCase
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Status

@Controller("/users")
class UserController(
    private val createUserUseCase: CreateUserUseCase
) {

    @Post("/")
    @Status(HttpStatus.CREATED)
    fun create(@Body request: CreateUserRequest): CreateUserResponse {
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
