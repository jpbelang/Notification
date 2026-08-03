package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.port.inbound.CreateOrganisationUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateOrganisationServiceTest : StringSpec({
    val repository = mockk<OrganisationRepository>()
    val service = CreateOrganisationService(repository)

    "should create and save a new organisation" {
        val name = "Test Org"
        every { repository.save(any<Organisation>()) } returns Unit

        val result = service.execute(CreateOrganisationUseCase.Command(name))

        result.name shouldBe name
        verify { repository.save(match { it.name == name }) }
    }
})
