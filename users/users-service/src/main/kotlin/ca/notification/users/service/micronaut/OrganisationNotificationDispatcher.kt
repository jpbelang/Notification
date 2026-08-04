package ca.notification.users.service.micronaut

import ca.notification.users.service.delivery.lambda.OrganisationNotificationHandler
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import io.micronaut.function.aws.MicronautRequestHandler
import jakarta.inject.Inject

class OrganisationNotificationDispatcher : MicronautRequestHandler<SQSEvent, Void?>() {

    @Inject
    lateinit var handler: OrganisationNotificationHandler

    override fun execute(input: SQSEvent): Void? {
        handler.handle(input)
        return null
    }
}
