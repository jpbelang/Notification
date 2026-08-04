package ca.notification.organisations.service.port.outbound

import ca.notification.organisations.service.domain.NotificationEvent

interface NotificationPublisher {
    fun publish(event: NotificationEvent)
}
