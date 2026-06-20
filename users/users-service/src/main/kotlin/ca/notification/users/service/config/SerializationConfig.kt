package ca.notification.users.service.config

import ca.notification.users.service.delivery.lambda.CreateUserRequest
import ca.notification.users.service.delivery.lambda.CreateUserResponse
import io.micronaut.serde.annotation.SerdeImport

@SerdeImport(CreateUserRequest::class)
@SerdeImport(CreateUserResponse::class)
class SerializationConfig
