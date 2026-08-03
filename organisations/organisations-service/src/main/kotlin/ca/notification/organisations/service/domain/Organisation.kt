package ca.notification.organisations.service.domain

import java.util.UUID

data class NewOrganisation(
    val name: String
)

enum class Role {
    ADMIN, MEMBER, VIEWER;

    companion object {
        fun fromString(value: String): Role = valueOf(value.uppercase())
    }
}

data class Participant(
    val id: UUID,
    val role: Role
)

data class Organisation(
    val id: TypedUUID<Organisation>,
    val name: String,
    val participants: List<Participant> = emptyList()
) {
    companion object {
        fun createNew(name: String) = NewOrganisation(name)

        fun from(
            id: TypedUUID<Organisation>,
            name: String,
            participants: List<Participant> = emptyList()
        ) = Organisation(id, name, participants)

        fun withId(newOrganisation: NewOrganisation, id: TypedUUID<Organisation>) = Organisation(
            id = id,
            name = newOrganisation.name,
            participants = emptyList()
        )
    }

    fun addParticipant(participant: Participant): Organisation {
        return copy(participants = participants + participant)
    }

    fun removeParticipant(participantId: UUID): Organisation {
        return copy(participants = participants.filter { it.id != participantId })
    }
}

class OrganisationNotFoundException(id: TypedUUID<Organisation>) : RuntimeException("Organisation with id $id not found")
