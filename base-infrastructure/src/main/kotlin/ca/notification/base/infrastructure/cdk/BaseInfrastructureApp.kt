package ca.notification.base.infrastructure.cdk

import software.amazon.awscdk.App

fun main() {
    val app = App()
    BaseInfrastructureStack(app, "BaseInfrastructureStack")
    app.synth()
}
