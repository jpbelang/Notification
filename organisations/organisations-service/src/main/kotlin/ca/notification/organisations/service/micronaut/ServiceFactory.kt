package ca.notification.organisations.service.micronaut

import ca.notification.organisations.service.application.*
import ca.notification.organisations.service.port.inbound.*
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

@Factory
class ServiceFactory {

    @Singleton
    fun createOrganisationUseCase(organisationRepository: OrganisationRepository): CreateOrganisationUseCase =
        CreateOrganisationService(organisationRepository)

    @Singleton
    fun findOrganisationUseCase(organisationRepository: OrganisationRepository): FindOrganisationUseCase =
        FindOrganisationService(organisationRepository)

    @Singleton
    fun updateOrganisationUseCase(organisationRepository: OrganisationRepository): UpdateOrganisationUseCase =
        UpdateOrganisationService(organisationRepository)

    @Singleton
    fun deleteOrganisationUseCase(organisationRepository: OrganisationRepository): DeleteOrganisationUseCase =
        DeleteOrganisationService(organisationRepository)

    @Singleton
    fun addParticipantUseCase(organisationRepository: OrganisationRepository): AddParticipantUseCase =
        AddParticipantService(organisationRepository)

    @Singleton
    fun removeParticipantUseCase(organisationRepository: OrganisationRepository): RemoveParticipantUseCase =
        RemoveParticipantService(organisationRepository)
}
