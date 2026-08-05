package ca.notification.organisations.service.domain

data class AuthenticatedUser(
    val id: String,
    val email: String,
    val name: String? = null
)
