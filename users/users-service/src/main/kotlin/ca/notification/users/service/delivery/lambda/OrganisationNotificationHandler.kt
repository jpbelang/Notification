package ca.notification.users.service.delivery.lambda

import ca.notification.users.service.port.inbound.ProcessOrganisationNotificationUseCase
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import org.slf4j.LoggerFactory

class OrganisationNotificationHandler(
    private val processOrganisationNotificationUseCase: ProcessOrganisationNotificationUseCase
) {
    private val logger = LoggerFactory.getLogger(OrganisationNotificationHandler::class.java)

    fun handle(event: SQSEvent) {
        event.records.forEach { record ->
            logger.info("Received message from SQS: ${record.body}")
            processOrganisationNotificationUseCase.execute(record.body)
        }
    }
}
