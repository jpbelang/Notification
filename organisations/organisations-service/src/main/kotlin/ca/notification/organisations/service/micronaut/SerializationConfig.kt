package ca.notification.organisations.service.micronaut

import ca.notification.organisations.service.delivery.lambda.OrganisationRequest
import ca.notification.organisations.service.delivery.lambda.OrganisationResponse
import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.Participant
import ca.notification.organisations.service.domain.TypedUUID
import io.micronaut.serde.annotation.SerdeImport

@SerdeImport(OrganisationRequest::class)
@SerdeImport(OrganisationResponse::class)
@SerdeImport(Organisation::class)
@SerdeImport(Participant::class)
@SerdeImport(TypedUUID::class)
class SerializationConfig
