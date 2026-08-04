package ca.notification.users.service.port.inbound

interface ProcessOrganisationNotificationUseCase {
    fun execute(payload: String)
}
