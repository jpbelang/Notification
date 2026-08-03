package ca.notification.organisations.service.port.inbound

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.TypedUUID

interface DeleteOrganisationUseCase {
    fun execute(id: TypedUUID<Organisation>)
}
