package ca.notification.organisations.service.micronaut

import ca.notification.organisations.service.delivery.lambda.AddParticipantRequest
import ca.notification.organisations.service.delivery.lambda.OrganisationHandler
import ca.notification.organisations.service.delivery.lambda.OrganisationRequest
import ca.notification.organisations.service.delivery.lambda.OrganisationResponse
import ca.notification.organisations.service.domain.OrganisationNotFoundException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.http.exceptions.HttpStatusException
import jakarta.inject.Singleton

@Singleton
@Controller("/organisations")
class OrganisationDispatcher(private val organisationHandler: OrganisationHandler) {

    @Post("/")
    @Status(HttpStatus.CREATED)
    fun create(@Body request: OrganisationRequest): OrganisationResponse {
        return organisationHandler.create(request)
    }

    @Get("/{id}")
    fun findById(id: String): OrganisationResponse {
        return organisationHandler.findById(id) ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "Organisation not found")
    }

    @Get("/")
    fun findAll(): List<OrganisationResponse> {
        return organisationHandler.findAll()
    }

    @Put("/{id}")
    fun update(id: String, @Body request: OrganisationRequest): OrganisationResponse {
        return organisationHandler.update(id, request)
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    fun delete(id: String) {
        organisationHandler.delete(id)
    }

    @Post("/{id}/participants")
    fun addParticipant(id: String, @Body request: AddParticipantRequest): OrganisationResponse {
        return organisationHandler.addParticipant(id, request)
    }

    @Delete("/{id}/participants/{participantId}")
    fun removeParticipant(id: String, participantId: String): OrganisationResponse {
        return organisationHandler.removeParticipant(id, participantId)
    }

    @Error(exception = OrganisationNotFoundException::class)
    fun handleNotFound(request: HttpRequest<*>, exception: OrganisationNotFoundException): HttpResponse<String> {
        return HttpResponse.status<String>(HttpStatus.NOT_FOUND).body(exception.message)
    }
}
