package ca.notification.users.infrastructure.cdk

import software.amazon.awscdk.Duration
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.apigateway.LambdaRestApi
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
        val createUserHandler = Function.Builder.create(this, "CreateUserHandler")
            .runtime(Runtime.JAVA_21)
            .handler("io.micronaut.function.aws.MicronautRequestHandler")
            .memorySize(512)
            .timeout(Duration.seconds(30))
            // This assumes the shadowJar task has been run and produced the fat JAR
            .code(Code.fromAsset("../users-service/build/libs/users-service-all.jar"))
            .environment(mapOf(
                "MICRONAUT_ENVIRONMENTS" to "lambda"
            ))
            .build()

        LambdaRestApi.Builder.create(this, "UsersApi")
            .handler(createUserHandler)
            .build()
    }
}
