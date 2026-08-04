package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.OrganisationPayload
import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.port.inbound.UpdateOrganisationUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import ca.notification.organisations.service.port.outbound.NotificationPublisher
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class UpdateOrganisationServiceTest : StringSpec({
    val repository = mockk<OrganisationRepository>()
    val publisher = mockk<NotificationPublisher>()
    val service = UpdateOrganisationService(repository, publisher)

    "should update, save and publish an organisation" {
        val id = TypedUUID.create<Organisation>()
        val originalOrg = Organisation.from(id, "Original Name")
        val newName = "New Name"
        
        every { repository.findById(id) } returns originalOrg
        every { repository.save(any()) } returns Unit
        every { publisher.publish(any()) } returns Unit

        val result = service.execute(UpdateOrganisationUseCase.Command(id, newName))

        result.name shouldBe newName
        verify { repository.save(match { it.name == newName && it.id == id }) }
        verify { publisher.publish(match { it.type == "UpdatedOrganisation" && (it.payload as OrganisationPayload).organisation.name == newName }) }
    }
})
