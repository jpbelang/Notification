package ca.notification.users.service.micronaut

import ca.notification.users.service.delivery.lambda.CreateUserRequest
import ca.notification.users.service.delivery.lambda.UserResponse
import io.micronaut.serde.annotation.SerdeImport

@SerdeImport(CreateUserRequest::class)
@SerdeImport(UserResponse::class)
class SerializationConfig
