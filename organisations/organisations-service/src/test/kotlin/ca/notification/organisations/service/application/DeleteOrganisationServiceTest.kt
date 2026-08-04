package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.OrganisationPayload
import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import ca.notification.organisations.service.port.outbound.NotificationPublisher
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class DeleteOrganisationServiceTest : StringSpec({
    val repository = mockk<OrganisationRepository>()
    val publisher = mockk<NotificationPublisher>()
    val service = DeleteOrganisationService(repository, publisher)

    "should delete and publish an organisation" {
        val id = TypedUUID.create<Organisation>()
        val org = Organisation.from(id, "Test Org")
        
        every { repository.findById(id) } returns org
        every { repository.delete(org) } returns Unit
        every { publisher.publish(any()) } returns Unit

        service.execute(id)

        verify { repository.delete(org) }
        verify { publisher.publish(match { it.type == "DeletedOrganisation" && (it.payload as OrganisationPayload).organisation.id == id }) }
    }
})
