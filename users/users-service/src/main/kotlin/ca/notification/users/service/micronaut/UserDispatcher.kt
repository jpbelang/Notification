package ca.notification.users.service.micronaut

import ca.notification.users.service.delivery.lambda.CreateUserRequest
import ca.notification.users.service.delivery.lambda.CreateUserResponse
import ca.notification.users.service.delivery.lambda.UserHandler
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Status
import jakarta.inject.Singleton

@Singleton
@Controller("/users")
class UserDispatcher(private val userHandler: UserHandler) {

    @Post("/")
    @Status(HttpStatus.CREATED)
    fun create(@Body request: CreateUserRequest): CreateUserResponse {
        return userHandler.create(request)
    }
}
