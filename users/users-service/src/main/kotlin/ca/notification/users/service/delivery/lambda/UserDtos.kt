package ca.notification.users.service.delivery.lambda

import io.micronaut.serde.annotation.Serdeable

@Serdeable
interface UserDto {
    val name: String
    val email: String
    val phoneNumber: String
}

@Serdeable
data class CreateUserRequest(
    override val name: String,
    override val email: String,
    override val phoneNumber: String,
    val password: String
) : UserDto

@Serdeable
data class CreateUserResponse(
    val id: String,
    override val name: String,
    override val email: String,
    override val phoneNumber: String
) : UserDto
