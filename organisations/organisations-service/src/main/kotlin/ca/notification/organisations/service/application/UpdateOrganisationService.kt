package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.NotificationEvent
import ca.notification.organisations.service.domain.OrganisationPayload
import ca.notification.organisations.service.domain.OrganisationNotFoundException
import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.port.inbound.UpdateOrganisationUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import ca.notification.organisations.service.port.outbound.NotificationPublisher

class UpdateOrganisationService(
    private val organisationRepository: OrganisationRepository,
    private val notificationPublisher: NotificationPublisher
) : UpdateOrganisationUseCase {
    override fun execute(command: UpdateOrganisationUseCase.Command): Organisation {
        val organisation = organisationRepository.findById(command.id) ?: throw OrganisationNotFoundException(command.id)
        val updatedOrganisation = organisation.copy(name = command.name)
        organisationRepository.save(updatedOrganisation)
        notificationPublisher.publish(NotificationEvent("UpdatedOrganisation", OrganisationPayload(updatedOrganisation)))
        return updatedOrganisation
    }
}
