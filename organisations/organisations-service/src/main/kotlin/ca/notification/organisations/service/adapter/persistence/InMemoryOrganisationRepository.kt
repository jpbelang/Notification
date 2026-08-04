package ca.notification.organisations.service.adapter.persistence

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.Participant
import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Singleton
@Requires(property = "micronaut.environment", notEquals = "lambda")
class InMemoryOrganisationRepository : OrganisationRepository {
    private val organisations = ConcurrentHashMap<TypedUUID<Organisation>, Organisation>()
    private val participants = ConcurrentHashMap<TypedUUID<Organisation>, MutableList<Participant>>()

    override fun save(organisation: Organisation) {
        organisations[organisation.id] = organisation
    }

    override fun findById(id: TypedUUID<Organisation>): Organisation? {
        return organisations[id]
    }

    override fun findAll(): List<Organisation> {
        return organisations.values.toList()
    }

    override fun delete(organisation: Organisation) {
        organisations.remove(organisation.id)
        participants.remove(organisation.id)
    }

    override fun addParticipant(organisationId: TypedUUID<Organisation>, participant: Participant) {
        participants.computeIfAbsent(organisationId) { mutableListOf() }.add(participant)
    }

    override fun removeParticipant(organisationId: TypedUUID<Organisation>, participantId: UUID) {
        participants[organisationId]?.removeIf { it.id == participantId }
    }
}
