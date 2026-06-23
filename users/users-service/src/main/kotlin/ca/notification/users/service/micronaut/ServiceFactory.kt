package ca.notification.users.service.micronaut

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import ca.notification.users.service.application.CreateUserService
import ca.notification.users.service.application.FindUserService
import ca.notification.users.service.application.UpdateUserService
import ca.notification.users.service.port.inbound.CreateUserUseCase
import ca.notification.users.service.port.inbound.FindUserUseCase
import ca.notification.users.service.port.inbound.UpdateUserUseCase
import ca.notification.users.service.port.outbound.UserRepository
import ca.notification.users.service.port.outbound.CredentialsRepository
import ca.notification.users.service.delivery.lambda.UserHandler
import io.micronaut.context.annotation.Primary
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

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
    fun findUserUseCase(userRepository: UserRepository): FindUserUseCase {
        return FindUserService(userRepository)
    }

    @Singleton
    fun updateUserUseCase(
        userRepository: UserRepository,
        credentialsRepository: CredentialsRepository
    ): UpdateUserUseCase {
        return UpdateUserService(userRepository, credentialsRepository)
    }

    @Singleton
    fun userHandler(
        createUserUseCase: CreateUserUseCase,
        findUserUseCase: FindUserUseCase,
        updateUserUseCase: UpdateUserUseCase
    ): UserHandler {
        return UserHandler(createUserUseCase, findUserUseCase, updateUserUseCase)
    }

    @Singleton
    fun cognitoClient():CognitoIdentityProviderClient {
        return CognitoIdentityProviderClient.builder().build()
    }

    @Singleton @Primary
    fun dynamoDbClient(): DynamoDbClient {
        print("building client")
        return DynamoDbClient.builder().build()
    }

}
