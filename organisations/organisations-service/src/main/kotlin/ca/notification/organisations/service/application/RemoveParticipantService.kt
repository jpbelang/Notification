package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.OrganisationNotFoundException
import ca.notification.organisations.service.port.inbound.RemoveParticipantUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import jakarta.inject.Singleton

@Singleton
class RemoveParticipantService(private val repository: OrganisationRepository) : RemoveParticipantUseCase {
    override fun execute(command: RemoveParticipantUseCase.Command): Organisation {
        val organisation = repository.findById(command.organisationId)
            ?: throw OrganisationNotFoundException(command.organisationId)

        val updatedOrganisation = organisation.removeParticipant(command.participantId)

        repository.save(updatedOrganisation)
        return updatedOrganisation
    }
}
