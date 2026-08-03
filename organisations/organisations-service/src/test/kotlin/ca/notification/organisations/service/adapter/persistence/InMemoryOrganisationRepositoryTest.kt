package ca.notification.organisations.service.adapter.persistence

import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.Participant
import ca.notification.organisations.service.domain.Role
import ca.notification.organisations.service.domain.TypedUUID
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.UUID

class InMemoryOrganisationRepositoryTest : StringSpec({
    val repository = InMemoryOrganisationRepository()

    "should save and find organisation" {
        val orgId = TypedUUID.create<Organisation>()
        val organisation = Organisation.from(orgId, "Test Org")

        repository.save(organisation)
        val result = repository.findById(orgId)

        result shouldNotBe null
        result?.name shouldBe "Test Org"
    }

    "should not overwrite participants on save if they exist" {
        val orgId = TypedUUID.create<Organisation>()
        val partId = UUID.randomUUID()
        val organisation = Organisation.from(orgId, "Test Org")

        repository.save(organisation)
        repository.addParticipant(orgId, Participant(partId, Role.ADMIN))

        val updatedOrg = Organisation.from(orgId, "New Name")
        repository.save(updatedOrg)

        val result = repository.findById(orgId)
        result?.name shouldBe "New Name"
        result?.participants?.size shouldBe 1
        result?.participants?.get(0)?.id shouldBe partId
    }

    "should add and remove participants" {
        val orgId = TypedUUID.create<Organisation>()
        val partId = UUID.randomUUID()
        val organisation = Organisation.from(orgId, "Test Org")

        repository.save(organisation)
        repository.addParticipant(orgId, Participant(partId, Role.ADMIN))

        var result = repository.findById(orgId)
        result?.participants?.size shouldBe 1

        repository.removeParticipant(orgId, partId)
        result = repository.findById(orgId)
        result?.participants?.size shouldBe 0
    }
})
