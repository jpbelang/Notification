package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.port.inbound.CreateOrganisationUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import ca.notification.organisations.service.port.outbound.NotificationPublisher
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateOrganisationServiceTest : StringSpec({
    val repository = mockk<OrganisationRepository>()
    val publisher = mockk<NotificationPublisher>()
    val service = CreateOrganisationService(repository, publisher)

    "should create, save and publish a new organisation" {
        val name = "Test Org"
        every { repository.save(any<Organisation>()) } returns Unit
        every { publisher.publish(any()) } returns Unit

        val result = service.execute(CreateOrganisationUseCase.Command(name))

        result.name shouldBe name
        verify { repository.save(match { it.name == name }) }
        verify { publisher.publish(match { it.type == "NewOrganisation" && (it.payload as Organisation).name == name }) }
    }
})
