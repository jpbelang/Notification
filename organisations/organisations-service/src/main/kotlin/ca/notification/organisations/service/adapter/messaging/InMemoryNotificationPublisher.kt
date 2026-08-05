package ca.notification.organisations.service.adapter.messaging

import ca.notification.organisations.service.domain.NotificationEvent
import ca.notification.organisations.service.port.outbound.NotificationPublisher
import java.util.concurrent.ConcurrentLinkedQueue

class InMemoryNotificationPublisher : NotificationPublisher {
    private val events = ConcurrentLinkedQueue<NotificationEvent<*>>()

    override fun publish(event: NotificationEvent<*>) {
        events.add(event)
    }

    fun getEvents(): List<NotificationEvent<*>> = events.toList()
    fun clear() = events.clear()
}
