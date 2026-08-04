package ca.notification.users.infrastructure.cdk

import ca.notification.base.infrastructure.cdk.BaseStackInfo
import software.amazon.awscdk.CfnOutput
import software.amazon.awscdk.Duration
import software.amazon.awscdk.RemovalPolicy
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.apigateway.LambdaRestApi
import software.amazon.awscdk.services.cognito.*
import software.amazon.awscdk.services.dynamodb.Attribute
import software.amazon.awscdk.services.dynamodb.AttributeType
import software.amazon.awscdk.services.dynamodb.GlobalSecondaryIndexProps
import software.amazon.awscdk.services.dynamodb.Table
import software.amazon.awscdk.services.events.EventBus
import software.amazon.awscdk.services.events.EventPattern
import software.amazon.awscdk.services.events.Rule
import software.amazon.awscdk.services.events.targets.SqsQueue
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.Runtime
import software.amazon.awscdk.services.lambda.eventsources.SqsEventSource
import software.amazon.awscdk.services.sqs.Queue
import software.constructs.Construct

class UsersInfrastructureStack(
    scope: Construct,
    id: String,
    props: StackProps? = null
) : Stack(scope, id, props) {
    init {
        val usersTable = Table.Builder.create(this, "UsersTable")
            .partitionKey(Attribute.builder().name("pk").type(AttributeType.STRING).build())
            .sortKey(Attribute.builder().name("sk").type(AttributeType.STRING).build())
            .removalPolicy(RemovalPolicy.DESTROY)
            .build()

        usersTable.addGlobalSecondaryIndex(
            GlobalSecondaryIndexProps.builder()
                .indexName("gsipk-gsisk-index")
                .partitionKey(Attribute.builder().name("gsipk").type(AttributeType.STRING).build())
                .sortKey(Attribute.builder().name("gsisk").type(AttributeType.STRING).build())
                .build()
        )

        val userPool = UserPool.Builder.create(this, "UserPool")
            .selfSignUpEnabled(true)
            .signInAliases(SignInAliases.builder().email(true).build())
            .autoVerify(AutoVerifiedAttrs.builder().email(true).build())
            .customAttributes(mapOf(
                "userId" to StringAttribute.Builder.create().mutable(true).build()
            ))
            .removalPolicy(RemovalPolicy.DESTROY)
            .build()

        val clientProps = UserPoolClientProps.builder()
            .userPool(userPool)
            .authFlows(
                AuthFlow.builder()
                    .adminUserPassword(true)
                    .userPassword(true)
                    .custom(true)
                    .userSrp(true).build()

            ).build()

        val client = userPool.addClient(
            "web", clientProps
        )

        val usersHandler = Function.Builder.create(this, "UsersHandler")
            .runtime(Runtime.JAVA_25)
            .handler("io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction")
            .memorySize(512)
            .timeout(Duration.seconds(30))
            // This assumes the shadowJar task has been run and produced the fat JAR
            .code(Code.fromAsset("../users-service/build/libs/users-service-all.jar"))
            .environment(mapOf(
                "MICRONAUT_ENVIRONMENT" to "lambda",
                "DYNAMODB_TABLE_NAME" to usersTable.tableName,
                "COGNITO_USER_POOL_ID" to userPool.userPoolId,
                "COGNITO_USER_POOL_CLIENT_ID" to client.userPoolClientId,
                "NOTIFICATION_BUS_NAME" to BaseStackInfo.eventBusName()
            ))
            .build()

        usersTable.grantReadWriteData(usersHandler)
        userPool.grant(usersHandler, "cognito-idp:AdminCreateUser")

        LambdaRestApi.Builder.create(this, "UsersApi")
            .handler(usersHandler)
            .build()

        CfnOutput.Builder.create(this, "UsersTableName")
            .value(usersTable.tableName)
            .build()

        CfnOutput.Builder.create(this, "CognitoUserPoolId")
            .value(userPool.userPoolId)
            .build()

        CfnOutput.Builder.create(this, "UsersHandlerName")
            .value(usersHandler.functionName)
            .build()

        val notificationBus = EventBus.fromEventBusName(this, "NotificationBusImport", BaseStackInfo.eventBusName())

        val notificationQueue = Queue.Builder.create(this, "OrganisationNotificationQueue")
            .visibilityTimeout(Duration.seconds(30))
            .build()

        Rule.Builder.create(this, "NewOrganisationRule")
            .eventBus(notificationBus)
            .eventPattern(EventPattern.builder()
                .detailType(listOf("NewOrganisation"))
                .build())
            .targets(listOf(SqsQueue.Builder.create(notificationQueue).build()))
            .build()

        val notificationHandler = Function.Builder.create(this, "OrganisationNotificationHandler")
            .runtime(Runtime.JAVA_25)
            .handler("ca.notification.users.service.micronaut.OrganisationNotificationDispatcher")
            .memorySize(512)
            .timeout(Duration.seconds(30))
            .code(Code.fromAsset("../users-service/build/libs/users-service-all.jar"))
            .environment(mapOf(
                "MICRONAUT_ENVIRONMENT" to "lambda"
            ))
            .build()

        notificationHandler.addEventSource(SqsEventSource.Builder.create(notificationQueue).build())
    }
}
