package ca.notification.base.infrastructure.cdk

import software.amazon.awscdk.CfnOutput
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.events.EventBus
import software.constructs.Construct

class BaseInfrastructureStack(
    scope: Construct,
    id: String,
    props: StackProps? = null
) : Stack(scope, id, props) {
    init {
        val notificationBus = EventBus.Builder.create(this, "NotificationBus")
            .eventBusName(BaseStackInfo.eventBusName())
            .build()

        CfnOutput.Builder.create(this, "NotificationBusName")
            .value(notificationBus.eventBusName)
            .build()

        CfnOutput.Builder.create(this, "NotificationBusArn")
            .value(notificationBus.eventBusArn)
            .build()
    }
}


