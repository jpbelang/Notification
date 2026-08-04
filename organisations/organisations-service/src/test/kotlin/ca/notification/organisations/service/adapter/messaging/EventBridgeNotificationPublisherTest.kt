package ca.notification.organisations.service.adapter.messaging

import ca.notification.organisations.service.domain.NotificationEvent
import ca.notification.organisations.service.domain.Organisation
import ca.notification.organisations.service.domain.TypedUUID
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.micronaut.json.JsonMapper
import io.micronaut.serde.ObjectMapper
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import software.amazon.awssdk.services.eventbridge.EventBridgeClient
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest

@MicronautTest
class EventBridgeNotificationPublisherTest(
    private val objectMapper: ObjectMapper
) : StringSpec({

    val eventBridgeClient = mockk<EventBridgeClient>()
    val busName = "TestBus"
    val publisher = EventBridgeNotificationPublisher(eventBridgeClient, objectMapper, busName)

    "should serialize payload and call putEvents" {
        val org = Organisation.from(TypedUUID.create(), "Test Org")
        val event = NotificationEvent("NewOrganisation", org)
        
        every { eventBridgeClient.putEvents(any<PutEventsRequest>()) } returns mockk()

        publisher.publish(event)

        verify {
            eventBridgeClient.putEvents(withArg<PutEventsRequest> { request ->
                request.entries().size shouldBe 1
                val entry = request.entries()[0]
                entry.eventBusName() shouldBe busName
                entry.detailType() shouldBe "NewOrganisation"
                entry.detail() shouldContain "\"name\":\"Test Org\""
                entry.detail() shouldContain "\"id\":"
            })
        }
    }
})
