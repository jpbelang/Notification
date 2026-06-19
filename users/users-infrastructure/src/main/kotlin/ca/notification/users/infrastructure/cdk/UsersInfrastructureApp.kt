package ca.notification.users.infrastructure.cdk

import software.amazon.awscdk.App

fun main() {
    val app = App()
    UsersInfrastructureStack(app, "UsersInfrastructureStack")
    app.synth()
}
