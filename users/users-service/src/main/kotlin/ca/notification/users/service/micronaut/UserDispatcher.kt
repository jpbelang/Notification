package ca.notification.users.service.micronaut

import ca.notification.users.service.delivery.lambda.CreateUserRequest
import ca.notification.users.service.delivery.lambda.CreateUserResponse
import ca.notification.users.service.delivery.lambda.UserHandler
import ca.notification.users.service.domain.UserExistsException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
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

    @Error(exception = UserExistsException::class)
    fun handleUserExists(request: HttpRequest<*>, exception: UserExistsException): HttpResponse<String> {
        return HttpResponse.status<String>(HttpStatus.CONFLICT).body(exception.message)
    }
}
