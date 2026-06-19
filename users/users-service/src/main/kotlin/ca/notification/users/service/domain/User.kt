package ca.notification.users.service.domain

data class User(
    val id: TypedUUID<User>,
    val name: String,
    val email: String,
    val phoneNumber: String
)
