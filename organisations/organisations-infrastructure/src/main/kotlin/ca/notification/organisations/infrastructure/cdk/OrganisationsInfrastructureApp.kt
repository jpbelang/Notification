package ca.notification.organisations.infrastructure.cdk

import software.amazon.awscdk.App

fun main() {
    val app = App()
    OrganisationsInfrastructureStack(app, "OrganisationsInfrastructureStack")
    app.synth()
}
