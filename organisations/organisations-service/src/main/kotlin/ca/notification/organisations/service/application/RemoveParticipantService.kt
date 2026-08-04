package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.NotificationEvent
import ca.notification.organisations.service.domain.OrganisationNotFoundException
import ca.notification.organisations.service.port.inbound.RemoveParticipantUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import ca.notification.organisations.service.port.outbound.NotificationPublisher
import jakarta.inject.Singleton

@Singleton
class RemoveParticipantService(
    private val repository: OrganisationRepository,
    private val notificationPublisher: NotificationPublisher
) : RemoveParticipantUseCase {
    override fun execute(command: RemoveParticipantUseCase.Command): Organisation {
        val organisation = repository.findById(command.organisationId)
            ?: throw OrganisationNotFoundException(command.organisationId)

        repository.removeParticipant(command.organisationId, command.participantId)

        val updatedOrg = organisation.removeParticipant(command.participantId)
        notificationPublisher.publish(NotificationEvent("UpdatedOrganisation", updatedOrg))
        return updatedOrg
    }
}
