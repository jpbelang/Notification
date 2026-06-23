package ca.notification.users.service.micronaut

import ca.notification.users.service.delivery.lambda.CreateUserRequest
import ca.notification.users.service.delivery.lambda.UserResponse
import ca.notification.users.service.delivery.lambda.UserHandler
import ca.notification.users.service.domain.UserExistsException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.http.exceptions.HttpStatusException
import jakarta.inject.Singleton

@Singleton
@Controller("/users")
class UserDispatcher(private val userHandler: UserHandler) {

    @Post("/")
    @Status(HttpStatus.CREATED)
    fun create(@Body request: CreateUserRequest): UserResponse {
        return userHandler.create(request)
    }

    @Get("/{id}")
    fun getById(id: String): UserResponse {
        return userHandler.getById(id) ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "User not found")
    }

    @Get("/email/{email}")
    fun getByEmail(email: String): UserResponse {
        return userHandler.getByEmail(email) ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "User not found")
    }

    @Error(exception = UserExistsException::class)
    fun handleUserExists(request: HttpRequest<*>, exception: UserExistsException): HttpResponse<String> {
        return HttpResponse.status<String>(HttpStatus.CONFLICT).body(exception.message)
    }
}
