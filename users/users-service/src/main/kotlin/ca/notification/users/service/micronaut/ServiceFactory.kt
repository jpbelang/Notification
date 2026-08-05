package ca.notification.users.service.micronaut

import ca.notification.users.service.port.inbound.ProcessOrganisationNotificationUseCase
import ca.notification.users.service.port.outbound.UserRepository
import ca.notification.users.service.port.outbound.CredentialsRepository
import ca.notification.users.service.adapter.persistence.DynamoUserRepository
import ca.notification.users.service.adapter.persistence.InMemoryUserRepository
import ca.notification.users.service.adapter.persistence.CognitoCredentialsRepository
import ca.notification.users.service.adapter.persistence.InMemoryCredentialsRepository
import ca.notification.users.service.delivery.lambda.UserHandler
import ca.notification.users.service.delivery.lambda.OrganisationNotificationHandler
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import jakarta.inject.Provider
import ca.notification.users.service.application.AuthenticateUserService
import ca.notification.users.service.application.CreateUserService
import ca.notification.users.service.application.DeleteUserService
import ca.notification.users.service.application.FindUserService
import ca.notification.users.service.application.UpdateUserService
import ca.notification.users.service.application.ProcessOrganisationNotificationService
import ca.notification.users.service.port.inbound.AuthenticateUserUseCase
import ca.notification.users.service.port.inbound.CreateUserUseCase
import ca.notification.users.service.port.inbound.DeleteUserUseCase
import ca.notification.users.service.port.inbound.FindUserUseCase
import ca.notification.users.service.port.inbound.UpdateUserUseCase
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
    fun deleteUserUseCase(
        userRepository: UserRepository,
        credentialsRepository: CredentialsRepository
    ): DeleteUserUseCase {
        return DeleteUserService(userRepository, credentialsRepository)
    }

    @Singleton
    fun authenticateUserUseCase(
        userRepository: UserRepository,
        credentialsRepository: CredentialsRepository
    ): AuthenticateUserUseCase {
        return AuthenticateUserService(userRepository, credentialsRepository)
    }

    @Singleton
    fun processOrganisationNotificationUseCase(): ProcessOrganisationNotificationUseCase {
        return ProcessOrganisationNotificationService()
    }

    @Singleton
    @Requires(property = "micronaut.environment", value = "lambda")
    fun userRepository(
        dynamoDbClient: DynamoDbClient,
        @Property(name = "dynamodb.table-name") tableName: String
    ): DynamoUserRepository {
        return DynamoUserRepository(dynamoDbClient, tableName)
    }

    @Singleton
    @Requires(property = "micronaut.environment", value = "local", defaultValue = "local")
    fun inMemoryUserRepository(): InMemoryUserRepository {
        return InMemoryUserRepository()
    }

    @Singleton
    @Requires(property = "micronaut.environment", value = "lambda")
    fun credentialsRepository(
        cognitoClient: CognitoIdentityProviderClient,
        @Property(name = "cognito.user-pool-id") userPoolId: String,
        @Property(name = "cognito.user-pool-client-id") userPoolClientId: String
    ): CognitoCredentialsRepository {
        return CognitoCredentialsRepository(cognitoClient, userPoolId, userPoolClientId)
    }

    @Singleton
    @Requires(property = "micronaut.environment", value = "local", defaultValue = "local")
    fun inMemoryCredentialsRepository(): InMemoryCredentialsRepository {
        return InMemoryCredentialsRepository()
    }

    @Singleton
    fun userHandler(
        createUserUseCase: CreateUserUseCase,
        findUserUseCase: FindUserUseCase,
        updateUserUseCase: UpdateUserUseCase,
        deleteUserUseCase: DeleteUserUseCase,
        authenticateUserUseCase: AuthenticateUserUseCase
    ): UserHandler {
        return UserHandler(
            createUserUseCase,
            findUserUseCase,
            updateUserUseCase,
            deleteUserUseCase,
            authenticateUserUseCase
        )
    }

    @Singleton
    fun organisationNotificationHandler(
        processOrganisationNotificationUseCase: ProcessOrganisationNotificationUseCase
    ): OrganisationNotificationHandler {
        return OrganisationNotificationHandler(processOrganisationNotificationUseCase)
    }

    @Singleton
    fun cognitoClient():CognitoIdentityProviderClient {
        return CognitoIdentityProviderClient.builder().build()
    }

    @Singleton @Primary
    fun dynamoDbClient(): DynamoDbClient {
        return DynamoDbClient.builder().build()
    }

}
