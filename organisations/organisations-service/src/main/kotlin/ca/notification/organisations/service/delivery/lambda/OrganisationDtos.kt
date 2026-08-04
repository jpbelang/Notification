package ca.notification.organisations.service.delivery.lambda

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class OrganisationRequest(
    val name: String
)

@Serdeable
data class OrganisationResponse(
    val id: String,
    val name: String
)

@Serdeable
data class ParticipantResponse(
    val id: String,
    val role: String
)

@Serdeable
data class AddParticipantRequest(
    val participantId: String,
    val role: String
)
