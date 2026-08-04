package ca.notification.organisations.service.domain

import java.util.UUID

data class NotificationEvent<T>(
    val type: String,
    val payload: T
)

data class OrganisationPayload(
    val organisation: Organisation
)

data class OrganisationParticipantAddedPayload(
    val organisation: Organisation,
    val participant: Participant
)

data class OrganisationParticipantRemovedPayload(
    val organisation: Organisation,
    val participantId: UUID
)
