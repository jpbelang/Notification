package ca.notification.users.infrastructure.cdk

import software.amazon.awscdk.Duration
import software.amazon.awscdk.RemovalPolicy
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.apigateway.LambdaRestApi
import software.amazon.awscdk.services.dynamodb.Attribute
import software.amazon.awscdk.services.dynamodb.AttributeType
import software.amazon.awscdk.services.dynamodb.Table
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.Runtime
import software.constructs.Construct

class UsersInfrastructureStack(
    scope: Construct,
    id: String,
    props: StackProps? = null
) : Stack(scope, id, props) {
    init {
        val usersTable = Table.Builder.create(this, "UsersTable")
            .partitionKey(Attribute.builder().name("id").type(AttributeType.STRING).build())
            .removalPolicy(RemovalPolicy.DESTROY)
            .build()

        val createUserHandler = Function.Builder.create(this, "CreateUserHandler")
            .runtime(Runtime.JAVA_21)
            .handler("io.micronaut.function.aws.MicronautRequestHandler")
            .memorySize(512)
            .timeout(Duration.seconds(30))
            // This assumes the shadowJar task has been run and produced the fat JAR
            .code(Code.fromAsset("../users-service/build/libs/users-service-all.jar"))
            .environment(mapOf(
                "MICRONAUT_ENVIRONMENTS" to "lambda",
                "PERSISTENCE_TYPE" to "dynamodb",
                "PERSISTENCE_DYNAMODB_TABLE_NAME" to usersTable.tableName,
            ))
            .build()

        usersTable.grantReadWriteData(createUserHandler)

        LambdaRestApi.Builder.create(this, "UsersApi")
            .handler(createUserHandler)
            .build()
    }
}
