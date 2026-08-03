package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.OrganisationNotFoundException
import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.port.inbound.DeleteOrganisationUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository

class DeleteOrganisationService(
    private val organisationRepository: OrganisationRepository
) : DeleteOrganisationUseCase {
    override fun execute(id: TypedUUID<Organisation>) {
        val organisation = organisationRepository.findById(id) ?: throw OrganisationNotFoundException(id)
        organisationRepository.delete(organisation)
    }
}
