package ca.notification.users.service.domain

data class AuthTokens(
    val accessToken: String,
    val idToken: String,
    val refreshToken: String?,
    val expiresIn: Int
)
