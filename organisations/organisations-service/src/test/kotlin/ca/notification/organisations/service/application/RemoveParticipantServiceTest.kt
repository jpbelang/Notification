package ca.notification.organisations.service.application

import ca.notification.organisations.service.domain.*
import ca.notification.organisations.service.port.inbound.RemoveParticipantUseCase
import ca.notification.organisations.service.port.outbound.OrganisationRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class RemoveParticipantServiceTest : StringSpec({
    val repository = mockk<OrganisationRepository>()
    val service = RemoveParticipantService(repository)

    "should remove a participant from an organisation" {
        val orgId = TypedUUID.create<Organisation>()
        val participantId = UUID.randomUUID()
        val organisation = Organisation.from(orgId, "Test Org", listOf(Participant(participantId, Role.MEMBER)))

        every { repository.findById(orgId) } returns organisation
        every { repository.removeParticipant(any(), any()) } returns Unit

        val result = service.execute(RemoveParticipantUseCase.Command(orgId, participantId))

        result.participants.size shouldBe 0

        verify { repository.removeParticipant(orgId, participantId) }
    }
})
