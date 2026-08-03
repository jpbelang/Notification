package ca.notification.organisations.service.port.inbound

import ca.notification.organisations.service.domain.Organisation

interface CreateOrganisationUseCase {
    fun execute(command: Command): Organisation

    data class Command(
        val name: String
    )
}
