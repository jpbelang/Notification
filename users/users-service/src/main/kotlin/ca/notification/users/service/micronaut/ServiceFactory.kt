package ca.notification.users.service.micronaut

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import ca.notification.users.service.application.CreateUserService
import ca.notification.users.service.port.inbound.CreateUserUseCase
import ca.notification.users.service.port.outbound.UserRepository
import ca.notification.users.service.port.outbound.CredentialsRepository
import ca.notification.users.service.delivery.lambda.UserHandler
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient

@Factory
class ServiceFactory {

    @Singleton
    fun createUserUseCase(
        userRepository: UserRepository,
        credentialsRepository: CredentialsRepository
    ): CreateUserUseCase {
        return CreateUserService(userRepository, credentialsRepository)
    }

    @Singleton
    fun userHandler(createUserUseCase: CreateUserUseCase): UserHandler {
        return UserHandler(createUserUseCase)
    }

    @Singleton
    fun cognitoClient():CognitoIdentityProviderClient {
        return CognitoIdentityProviderClient.builder().build()
    }
}
