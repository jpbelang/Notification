package ca.notification.users.service.delivery.lambda

interface UserDto {
    val name: String
    val email: String
    val phoneNumber: String
}

data class CreateUserRequest(
    override val name: String,
    override val email: String,
    override val phoneNumber: String,
    val password: String
) : UserDto

data class CreateUserResponse(
    val id: String,
    override val name: String,
    override val email: String,
    override val phoneNumber: String
) : UserDto
