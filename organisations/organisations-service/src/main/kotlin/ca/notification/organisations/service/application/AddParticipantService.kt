package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.OrganisationNotFoundException
import ca.notification.organisations.service.domain.Participant
import ca.notification.organisations.service.port.inbound.AddParticipantUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import jakarta.inject.Singleton

@Singleton
class AddParticipantService(private val repository: OrganisationRepository) : AddParticipantUseCase {
    override fun execute(command: AddParticipantUseCase.Command): Organisation {
        val organisation = repository.findById(command.organisationId)
            ?: throw OrganisationNotFoundException(command.organisationId)

        val participant = Participant(id = command.participantId, role = command.role)
        repository.addParticipant(command.organisationId, participant)

        return organisation.addParticipant(participant)
    }
}
