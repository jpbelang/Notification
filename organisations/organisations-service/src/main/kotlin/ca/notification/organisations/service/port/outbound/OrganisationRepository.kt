package ca.notification.organisations.service.port.outbound

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.TypedUUID

interface OrganisationRepository {
    fun save(organisation: Organisation)
    fun findById(id: TypedUUID<Organisation>): Organisation?
    fun findAll(): List<Organisation>
    fun delete(organisation: Organisation)
}
