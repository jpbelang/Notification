package ca.notification.organisations.service.micronaut

import ca.notification.organisations.service.delivery.lambda.OrganisationRequest
import ca.notification.organisations.service.delivery.lambda.OrganisationResponse
import io.micronaut.serde.annotation.SerdeImport

@SerdeImport(OrganisationRequest::class)
@SerdeImport(OrganisationResponse::class)
class SerializationConfig
