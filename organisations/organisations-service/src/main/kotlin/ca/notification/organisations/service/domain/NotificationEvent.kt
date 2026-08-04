package ca.notification.organisations.service.domain

data class NotificationEvent(
    val type: String,
    val payload: Any
)
