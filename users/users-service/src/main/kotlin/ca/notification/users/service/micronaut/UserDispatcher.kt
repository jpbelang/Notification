package ca.notification.users.service.micronaut

import ca.notification.users.service.delivery.lambda.*
import ca.notification.users.service.domain.UserExistsException
import ca.notification.users.service.domain.UserNotFoundException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.http.cookie.Cookie
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

    @Put("/{id}")
    fun update(id: String, @Body request: UpdateUserRequest): UserResponse {
        return userHandler.update(id, request)
    }

    @Get("/{id}")
    fun getById(id: String): UserResponse {
        return userHandler.getById(id) ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "User not found")
    }

    @Get("/email/{email}")
    fun getByEmail(email: String): UserResponse {
        return userHandler.getByEmail(email) ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "User not found")
    }

    @Post("/email/{email}/authenticate")
    fun authenticate(email: String, @Body request: AuthenticateUserRequest): HttpResponse<UserResponse> {
        val result = userHandler.authenticate(email, request) ?: throw HttpStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")

        val userResponse = UserResponse(
            id = result.user.id.toString(),
            name = result.user.name,
            email = result.user.email,
            phoneNumber = result.user.phoneNumber
        )

        val accessTokenCookie = Cookie.of("accessToken", result.tokens.accessToken)
            .httpOnly(true)
            .path("/")
            .maxAge(result.tokens.expiresIn.toLong())

        val idTokenCookie = Cookie.of("idToken", result.tokens.idToken)
            .httpOnly(true)
            .path("/")
            .maxAge(result.tokens.expiresIn.toLong())

        var response = HttpResponse.ok(userResponse)
            .cookie(accessTokenCookie)
            .cookie(idTokenCookie)

        result.tokens.refreshToken?.let {
            val refreshTokenCookie = Cookie.of("refreshToken", it)
                .httpOnly(true)
                .path("/")
                .maxAge(30 * 24 * 60 * 60) // 30 days
            response = response.cookie(refreshTokenCookie)
        }

        return response
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    fun delete(id: String) {
        userHandler.delete(id)
    }

    @Error(exception = UserExistsException::class)
    fun handleUserExists(request: HttpRequest<*>, exception: UserExistsException): HttpResponse<String> {
        return HttpResponse.status<String>(HttpStatus.CONFLICT).body(exception.message)
    }

    @Error(exception = UserNotFoundException::class)
    fun handleUserNotFound(request: HttpRequest<*>, exception: UserNotFoundException): HttpResponse<String> {
        return HttpResponse.status<String>(HttpStatus.NOT_FOUND).body(exception.message)
    }
}
