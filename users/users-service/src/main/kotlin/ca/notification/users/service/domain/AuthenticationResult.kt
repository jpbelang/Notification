package ca.notification.users.service.domain

data class AuthenticationResult(
    val user: User,
    val tokens: AuthTokens
)
