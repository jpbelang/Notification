package ca.notification.organisations.service.delivery.lambda

import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.port.inbound.*
import jakarta.inject.Singleton

@Singleton
class OrganisationHandler(
    private val createOrganisationUseCase: CreateOrganisationUseCase,
    private val findOrganisationUseCase: FindOrganisationUseCase,
    private val updateOrganisationUseCase: UpdateOrganisationUseCase,
    private val deleteOrganisationUseCase: DeleteOrganisationUseCase
) {
    fun create(request: OrganisationRequest): OrganisationResponse {
        val organisation = createOrganisationUseCase.execute(
            CreateOrganisationUseCase.Command(name = request.name)
        )
        return OrganisationResponse(id = organisation.id.toString(), name = organisation.name)
    }

    fun findById(id: String): OrganisationResponse? {
        val organisation = findOrganisationUseCase.findById(TypedUUID.fromString(id))
        return organisation?.let { OrganisationResponse(id = it.id.toString(), name = it.name) }
    }

    fun findAll(): List<OrganisationResponse> {
        return findOrganisationUseCase.findAll().map { OrganisationResponse(id = it.id.toString(), name = it.name) }
    }

    fun update(id: String, request: OrganisationRequest): OrganisationResponse {
        val organisation = updateOrganisationUseCase.execute(
            UpdateOrganisationUseCase.Command(id = TypedUUID.fromString(id), name = request.name)
        )
        return OrganisationResponse(id = organisation.id.toString(), name = organisation.name)
    }

    fun delete(id: String) {
        deleteOrganisationUseCase.execute(TypedUUID.fromString(id))
    }
}
