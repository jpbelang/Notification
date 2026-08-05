package ca.notification.users.service.micronaut

import ca.notification.users.service.delivery.lambda.OrganisationNotificationHandler
import ca.notification.users.service.port.inbound.ProcessOrganisationNotificationUseCase
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.mockk.verify
import io.micronaut.context.ApplicationContext
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest
class OrganisationNotificationDispatcherTest(
    private val applicationContext: ApplicationContext
) : StringSpec({

    "should process SQS event via Micronaut dispatcher" {
        try {
            System.setProperty("aws.region", "us-east-1")
            val dispatcher = applicationContext.createBean(OrganisationNotificationDispatcher::class.java)

            val event = SQSEvent().apply {
                records = listOf(
                    SQSEvent.SQSMessage().apply {
                        body = "hello from dispatcher test"
                    }
                )
            }

            dispatcher.execute(event)

            // Success if no exception is thrown and wiring worked
        } finally {
            System.clearProperty("aws.region")
        }
    }
})

