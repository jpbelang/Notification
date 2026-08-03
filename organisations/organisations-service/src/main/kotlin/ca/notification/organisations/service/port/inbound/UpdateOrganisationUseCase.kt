package ca.notification.organisations.service.port.inbound

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.TypedUUID

interface UpdateOrganisationUseCase {
    fun execute(command: Command): Organisation

    data class Command(
        val id: TypedUUID<Organisation>,
        val name: String
    )
}
