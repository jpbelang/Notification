package ca.notification.organisations.service.adapter.messaging

import ca.notification.organisations.service.domain.NotificationEvent
import ca.notification.organisations.service.port.outbound.NotificationPublisher
import io.micronaut.serde.ObjectMapper
import software.amazon.awssdk.services.eventbridge.EventBridgeClient
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry

class EventBridgeNotificationPublisher(
    private val eventBridgeClient: EventBridgeClient,
    private val objectMapper: ObjectMapper,
    private val busName: String
) : NotificationPublisher {

    override fun publish(event: NotificationEvent<*>) {
        val detail = objectMapper.writeValueAsString(event.payload)
        
        val entry = PutEventsRequestEntry.builder()
            .eventBusName(busName)
            .detailType(event.type)
            .detail(detail)
            .source("ca.notification.organisations")
            .build()

        val request = PutEventsRequest.builder()
            .entries(entry)
            .build()

        eventBridgeClient.putEvents(request)
    }
}
