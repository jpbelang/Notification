package ca.notification.organisations.service.domain

import java.util.UUID

data class NotificationEvent(
    val type: String,
    val payload: Any
)

data class OrganisationParticipantAddedPayload(
    val organisation: Organisation,
    val participant: Participant
)

data class OrganisationParticipantRemovedPayload(
    val organisation: Organisation,
    val participantId: UUID
)
