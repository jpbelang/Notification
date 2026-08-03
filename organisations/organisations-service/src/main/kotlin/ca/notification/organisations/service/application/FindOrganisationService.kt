package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.port.inbound.FindOrganisationUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository

class FindOrganisationService(
    private val organisationRepository: OrganisationRepository
) : FindOrganisationUseCase {
    override fun findById(id: TypedUUID<Organisation>): Organisation? {
        return organisationRepository.findById(id)
    }

    override fun findAll(): List<Organisation> {
        return organisationRepository.findAll()
    }
}
