package ca.notification.organisations.service.adapter.messaging

import ca.notification.organisations.service.domain.NotificationEvent
import ca.notification.organisations.service.port.outbound.NotificationPublisher
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentLinkedQueue

@Singleton
@Requires(missingProperty = "notification.bus.name")
class InMemoryNotificationPublisher : NotificationPublisher {
    private val events = ConcurrentLinkedQueue<NotificationEvent<*>>()

    override fun publish(event: NotificationEvent<*>) {
        events.add(event)
    }

    fun getEvents(): List<NotificationEvent<*>> = events.toList()
    fun clear() = events.clear()
}
