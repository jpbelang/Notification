package ca.notification.organisations.service.micronaut

import ca.notification.organisations.service.application.*
import ca.notification.organisations.service.port.inbound.*
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import ca.notification.organisations.service.port.outbound.NotificationPublisher
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

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
}
