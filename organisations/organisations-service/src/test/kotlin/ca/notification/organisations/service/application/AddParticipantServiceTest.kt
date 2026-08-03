package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.Role
import ca.notification.organisations.service.domain.TypedUUID
import ca.notification.organisations.service.port.inbound.AddParticipantUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class AddParticipantServiceTest : StringSpec({
    val repository = mockk<OrganisationRepository>()
    val service = AddParticipantService(repository)

    "should add a participant to an organisation" {
        val orgId = TypedUUID.create<Organisation>()
        val participantId = UUID.randomUUID()
        val role = Role.ADMIN
        val organisation = Organisation.from(orgId, "Test Org")

        every { repository.findById(orgId) } returns organisation
        every { repository.addParticipant(any(), any()) } returns Unit

        val result = service.execute(AddParticipantUseCase.Command(orgId, participantId, role))

        result.participants.size shouldBe 1
        result.participants[0].id shouldBe participantId
        result.participants[0].role shouldBe role

        verify { repository.addParticipant(orgId, match { it.id == participantId && it.role == role }) }
    }
})
