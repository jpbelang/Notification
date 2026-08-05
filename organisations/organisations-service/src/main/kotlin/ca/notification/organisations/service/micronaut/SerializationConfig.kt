package ca.notification.organisations.service.micronaut

import ca.notification.organisations.service.delivery.lambda.OrganisationRequest
import ca.notification.organisations.service.delivery.lambda.OrganisationResponse
import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.OrganisationPayload
import ca.notification.organisations.service.domain.OrganisationParticipantAddedPayload
import ca.notification.organisations.service.domain.OrganisationParticipantRemovedPayload
import ca.notification.organisations.service.domain.Participant
import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.domain.AuthenticatedUser
import io.micronaut.serde.annotation.SerdeImport

@SerdeImport(OrganisationRequest::class)
@SerdeImport(OrganisationResponse::class)
@SerdeImport(Organisation::class)
@SerdeImport(OrganisationPayload::class)
@SerdeImport(OrganisationParticipantAddedPayload::class)
@SerdeImport(OrganisationParticipantRemovedPayload::class)
@SerdeImport(Participant::class)
@SerdeImport(TypedUUID::class)
@SerdeImport(AuthenticatedUser::class)
class SerializationConfig
