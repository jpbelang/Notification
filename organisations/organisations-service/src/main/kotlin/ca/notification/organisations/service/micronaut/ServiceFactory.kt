package ca.notification.organisations.service.micronaut

import ca.notification.organisations.service.application.*
import ca.notification.organisations.service.port.inbound.*
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import ca.notification.organisations.service.port.outbound.NotificationPublisher
import ca.notification.organisations.service.adapter.persistence.DynamoOrganisationRepository
import ca.notification.organisations.service.adapter.persistence.InMemoryOrganisationRepository
import ca.notification.organisations.service.adapter.messaging.EventBridgeNotificationPublisher
import ca.notification.organisations.service.adapter.messaging.InMemoryNotificationPublisher
import ca.notification.organisations.service.delivery.lambda.OrganisationHandler
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Primary
import io.micronaut.serde.ObjectMapper
import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.eventbridge.EventBridgeClient

@Factory
class ServiceFactory {

    @Singleton
    fun createOrganisationUseCase(
        organisationRepository: OrganisationRepository,
        notificationPublisher: NotificationPublisher
    ): CreateOrganisationUseCase =
        CreateOrganisationService(organisationRepository, notificationPublisher)

    @Singleton
    fun findOrganisationUseCase(organisationRepository: OrganisationRepository): FindOrganisationUseCase =
        FindOrganisationService(organisationRepository)

    @Singleton
    fun updateOrganisationUseCase(
        organisationRepository: OrganisationRepository,
        notificationPublisher: NotificationPublisher
    ): UpdateOrganisationUseCase =
        UpdateOrganisationService(organisationRepository, notificationPublisher)

    @Singleton
    fun deleteOrganisationUseCase(
        organisationRepository: OrganisationRepository,
        notificationPublisher: NotificationPublisher
    ): DeleteOrganisationUseCase =
        DeleteOrganisationService(organisationRepository, notificationPublisher)

    @Singleton
    fun addParticipantUseCase(
        organisationRepository: OrganisationRepository,
        notificationPublisher: NotificationPublisher
    ): AddParticipantUseCase =
        AddParticipantService(organisationRepository, notificationPublisher)

    @Singleton
    fun removeParticipantUseCase(
        organisationRepository: OrganisationRepository,
        notificationPublisher: NotificationPublisher
    ): RemoveParticipantUseCase =
        RemoveParticipantService(organisationRepository, notificationPublisher)

    @Singleton
    fun organisationHandler(
        createOrganisationUseCase: CreateOrganisationUseCase,
        findOrganisationUseCase: FindOrganisationUseCase,
        updateOrganisationUseCase: UpdateOrganisationUseCase,
        deleteOrganisationUseCase: DeleteOrganisationUseCase,
        addParticipantUseCase: AddParticipantUseCase,
        removeParticipantUseCase: RemoveParticipantUseCase
    ): OrganisationHandler {
        return OrganisationHandler(
            createOrganisationUseCase,
            findOrganisationUseCase,
            updateOrganisationUseCase,
            deleteOrganisationUseCase,
            addParticipantUseCase,
            removeParticipantUseCase
        )
    }

    @Singleton
    @Requires(property = "micronaut.environment", value = "lambda")
    fun organisationRepository(
        dynamoDbClient: DynamoDbClient,
        @Property(name = "dynamodb.table-name") tableName: String
    ): DynamoOrganisationRepository {
        return DynamoOrganisationRepository(dynamoDbClient, tableName)
    }

    @Singleton
    @Requires(property = "micronaut.environment", notEquals = "lambda")
    fun inMemoryOrganisationRepository(): InMemoryOrganisationRepository {
        return InMemoryOrganisationRepository()
    }

    @Singleton
    @Requires(property = "notification.bus.name")
    fun notificationPublisher(
        eventBridgeClient: EventBridgeClient,
        objectMapper: ObjectMapper,
        @Property(name = "notification.bus.name") busName: String
    ): EventBridgeNotificationPublisher {
        return EventBridgeNotificationPublisher(eventBridgeClient, objectMapper, busName)
    }

    @Singleton
    @Requires(missingProperty = "notification.bus.name")
    fun inMemoryNotificationPublisher(): InMemoryNotificationPublisher {
        return InMemoryNotificationPublisher()
    }

    @Singleton @Primary
    fun dynamoDbClient(): DynamoDbClient {
        return DynamoDbClient.builder().build()
    }

    @Singleton
    fun eventBridgeClient(): EventBridgeClient {
        return EventBridgeClient.builder().build()
    }
}
