package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.port.inbound.CreateOrganisationUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository

class CreateOrganisationService(
    private val organisationRepository: OrganisationRepository
) : CreateOrganisationUseCase {
    override fun execute(command: CreateOrganisationUseCase.Command): Organisation {
        val organisation = Organisation.from(
            id = TypedUUID.create(),
            name = command.name
        )
        organisationRepository.save(organisation)
        return organisation
    }
}
