package ca.notification.organisations.service.port.inbound

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.Role
import ca.notification.organisations.service.domain.TypedUUID
import java.util.UUID

interface AddParticipantUseCase {
    data class Command(
        val organisationId: TypedUUID<Organisation>,
        val participantId: UUID,
        val role: Role
    )

    fun execute(command: Command): Organisation
}
