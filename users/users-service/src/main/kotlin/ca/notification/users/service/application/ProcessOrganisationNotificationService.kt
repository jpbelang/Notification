package ca.notification.users.service.application

import ca.notification.users.service.port.inbound.ProcessOrganisationNotificationUseCase
import org.slf4j.LoggerFactory

class ProcessOrganisationNotificationService : ProcessOrganisationNotificationUseCase {
    private val logger = LoggerFactory.getLogger(ProcessOrganisationNotificationService::class.java)

    override fun execute(payload: String) {
        logger.info("Processing organisation notification: $payload")
        println("hello")
    }
}
