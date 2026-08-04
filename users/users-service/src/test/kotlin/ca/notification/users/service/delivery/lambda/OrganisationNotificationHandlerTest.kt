package ca.notification.users.service.delivery.lambda

import ca.notification.users.service.port.inbound.ProcessOrganisationNotificationUseCase
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.mockk.verify

class OrganisationNotificationHandlerTest : StringSpec({

    val useCase = mockk<ProcessOrganisationNotificationUseCase>(relaxed = true)
    val handler = OrganisationNotificationHandler(useCase)

    "should handle SQS event and call use case" {
        val payload = "{\"detail-type\": \"NewOrganisation\", \"detail\": {}}"
        val event = SQSEvent().apply {
            records = listOf(
                SQSEvent.SQSMessage().apply {
                    body = payload
                }
            )
        }

        handler.handle(event)
        
        verify { useCase.execute(payload) }
    }
})
