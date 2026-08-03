package ca.notification.organisations.service.port.inbound

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.TypedUUID

interface FindOrganisationUseCase {
    fun findById(id: TypedUUID<Organisation>): Organisation?
    fun findAll(): List<Organisation>
}
