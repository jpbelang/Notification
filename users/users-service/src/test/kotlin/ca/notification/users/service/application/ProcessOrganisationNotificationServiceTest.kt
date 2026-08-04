package ca.notification.users.service.application

import io.kotest.core.spec.style.StringSpec

class ProcessOrganisationNotificationServiceTest : StringSpec({
    val service = ProcessOrganisationNotificationService()

    "should execute and print hello" {
        service.execute("some-payload")
        // No exception thrown, output verified manually or by absence of crash
    }
})
