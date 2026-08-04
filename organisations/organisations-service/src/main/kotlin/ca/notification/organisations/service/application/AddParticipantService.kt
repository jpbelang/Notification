package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.NotificationEvent
import ca.notification.organisations.service.domain.OrganisationParticipantAddedPayload
import ca.notification.organisations.service.domain.OrganisationNotFoundException
import ca.notification.organisations.service.domain.Participant
import ca.notification.organisations.service.port.inbound.AddParticipantUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import ca.notification.organisations.service.port.outbound.NotificationPublisher
import jakarta.inject.Singleton

@Singleton
class AddParticipantService(
    private val repository: OrganisationRepository,
    private val notificationPublisher: NotificationPublisher
) : AddParticipantUseCase {
    override fun execute(command: AddParticipantUseCase.Command): Organisation {
        val organisation = repository.findById(command.organisationId)
            ?: throw OrganisationNotFoundException(command.organisationId)

        val participant = Participant(id = command.participantId, role = command.role)
        repository.addParticipant(command.organisationId, participant)

        notificationPublisher.publish(NotificationEvent("OrganisationParticipantAdded", OrganisationParticipantAddedPayload(organisation, participant)))
        return organisation
    }
}
