package ca.notification.organisations.service.delivery.lambda

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.Role
import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.port.inbound.*
import java.util.UUID

class OrganisationHandler(
    private val createOrganisationUseCase: CreateOrganisationUseCase,
    private val findOrganisationUseCase: FindOrganisationUseCase,
    private val updateOrganisationUseCase: UpdateOrganisationUseCase,
    private val deleteOrganisationUseCase: DeleteOrganisationUseCase,
    private val addParticipantUseCase: AddParticipantUseCase,
    private val removeParticipantUseCase: RemoveParticipantUseCase
) {
    fun create(request: OrganisationRequest): OrganisationResponse {
        val organisation = createOrganisationUseCase.execute(
            CreateOrganisationUseCase.Command(name = request.name)
        )
        return organisation.toResponse()
    }

    fun findById(id: String): OrganisationResponse? {
        val organisation = findOrganisationUseCase.findById(TypedUUID.fromString(id))
        return organisation?.toResponse()
    }

    fun findAll(): List<OrganisationResponse> {
        return findOrganisationUseCase.findAll().map { it.toResponse() }
    }

    fun update(id: String, request: OrganisationRequest): OrganisationResponse {
        val organisation = updateOrganisationUseCase.execute(
            UpdateOrganisationUseCase.Command(id = TypedUUID.fromString(id), name = request.name)
        )
        return organisation.toResponse()
    }

    fun delete(id: String) {
        deleteOrganisationUseCase.execute(TypedUUID.fromString(id))
    }

    fun addParticipant(organisationId: String, request: AddParticipantRequest): OrganisationResponse {
        val organisation = addParticipantUseCase.execute(
            AddParticipantUseCase.Command(
                organisationId = TypedUUID.fromString(organisationId),
                participantId = UUID.fromString(request.participantId),
                role = Role.fromString(request.role)
            )
        )
        return organisation.toResponse()
    }

    fun removeParticipant(organisationId: String, participantId: String): OrganisationResponse {
        val organisation = removeParticipantUseCase.execute(
            RemoveParticipantUseCase.Command(
                organisationId = TypedUUID.fromString(organisationId),
                participantId = UUID.fromString(participantId)
            )
        )
        return organisation.toResponse()
    }

    private fun Organisation.toResponse() = OrganisationResponse(
        id = id.toString(),
        name = name
    )
}
