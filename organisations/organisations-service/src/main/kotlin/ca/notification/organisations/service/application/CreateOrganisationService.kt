package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.NotificationEvent
import ca.notification.organisations.service.domain.OrganisationPayload
import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.port.inbound.CreateOrganisationUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import ca.notification.organisations.service.port.outbound.NotificationPublisher

class CreateOrganisationService(
    private val organisationRepository: OrganisationRepository,
    private val notificationPublisher: NotificationPublisher
) : CreateOrganisationUseCase {
    override fun execute(command: CreateOrganisationUseCase.Command): Organisation {
        val organisation = Organisation.from(
            id = TypedUUID.create(),
            name = command.name
        )
        organisationRepository.save(organisation)
        notificationPublisher.publish(NotificationEvent("NewOrganisation", OrganisationPayload(organisation)))
        return organisation
    }
}
