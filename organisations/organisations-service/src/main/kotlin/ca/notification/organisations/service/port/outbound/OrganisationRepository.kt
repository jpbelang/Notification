package ca.notification.organisations.service.port.outbound

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.Participant
import ca.notification.organisations.service.domain.TypedUUID
import java.util.UUID

interface OrganisationRepository {
    fun save(organisation: Organisation)
    fun findById(id: TypedUUID<Organisation>): Organisation?
    fun findAll(): List<Organisation>
    fun delete(organisation: Organisation)
    fun addParticipant(organisationId: TypedUUID<Organisation>, participant: Participant)
    fun removeParticipant(organisationId: TypedUUID<Organisation>, participantId: UUID)
}
