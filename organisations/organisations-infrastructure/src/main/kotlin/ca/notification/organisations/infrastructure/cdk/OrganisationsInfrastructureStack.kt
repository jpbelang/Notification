package ca.notification.organisations.infrastructure.cdk

import ca.notification.base.infrastructure.cdk.BaseStackInfo
import software.amazon.awscdk.CfnOutput
import software.amazon.awscdk.Duration
import software.amazon.awscdk.RemovalPolicy
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.apigateway.LambdaRestApi
import software.amazon.awscdk.services.dynamodb.Attribute
import software.amazon.awscdk.services.dynamodb.AttributeType
import software.amazon.awscdk.services.dynamodb.GlobalSecondaryIndexProps
import software.amazon.awscdk.services.dynamodb.Table
import software.amazon.awscdk.services.events.EventBus
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.Runtime
import software.constructs.Construct

class OrganisationsInfrastructureStack(
    scope: Construct,
    id: String,
    props: StackProps? = null
) : Stack(scope, id, props) {
    init {
        val organisationsTable = Table.Builder.create(this, "OrganisationsTable")
            .partitionKey(Attribute.builder().name("pk").type(AttributeType.STRING).build())
            .sortKey(Attribute.builder().name("sk").type(AttributeType.STRING).build())
            .removalPolicy(RemovalPolicy.DESTROY)
            .build()

        organisationsTable.addGlobalSecondaryIndex(
            GlobalSecondaryIndexProps.builder()
                .indexName("gsipk-gsisk-index")
                .partitionKey(Attribute.builder().name("gsipk").type(AttributeType.STRING).build())
                .sortKey(Attribute.builder().name("gsisk").type(AttributeType.STRING).build())
                .build()
        )

        val organisationsHandler = Function.Builder.create(this, "OrganisationsHandler")
            .runtime(Runtime.JAVA_25)
            .handler("io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction")
            .memorySize(512)
            .timeout(Duration.seconds(30))
            .code(Code.fromAsset("../organisations-service/build/libs/organisations-service-all.jar"))
            .environment(mapOf(
                "MICRONAUT_ENVIRONMENT" to "lambda",
                "DYNAMODB_TABLE_NAME" to organisationsTable.tableName,
                "NOTIFICATION_BUS_NAME" to BaseStackInfo.eventBusName()
            ))
            .build()

        organisationsTable.grantReadWriteData(organisationsHandler)

        val notificationBus = EventBus.fromEventBusName(this, "NotificationBusImport", BaseStackInfo.eventBusName())
        notificationBus.grantPutEventsTo(organisationsHandler)

        LambdaRestApi.Builder.create(this, "OrganisationsApi")
            .handler(organisationsHandler)
            .build()

        CfnOutput.Builder.create(this, "OrganisationsTableName")
            .value(organisationsTable.tableName)
            .build()

        CfnOutput.Builder.create(this, "OrganisationsHandlerName")
            .value(organisationsHandler.functionName)
            .build()
    }
}
