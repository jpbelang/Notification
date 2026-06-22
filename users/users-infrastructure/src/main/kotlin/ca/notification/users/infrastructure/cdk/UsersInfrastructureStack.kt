package ca.notification.users.infrastructure.cdk

import software.amazon.awscdk.CfnOutput
import software.amazon.awscdk.Duration
import software.amazon.awscdk.RemovalPolicy
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.apigateway.LambdaRestApi
import software.amazon.awscdk.services.cognito.*
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

        val userPool = UserPool.Builder.create(this, "UserPool")
            .selfSignUpEnabled(true)
            .signInAliases(SignInAliases.builder().email(true).build())
            .autoVerify(AutoVerifiedAttrs.builder().email(true).build())
            .customAttributes(mapOf(
                "userId" to StringAttribute.Builder.create().mutable(true).build()
            ))
            .removalPolicy(RemovalPolicy.DESTROY)
            .build()

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
                "COGNITO_USER_POOL_ID" to userPool.userPoolId
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

        // Generate users-awsenv.json for SAM local
        val handlerLogicalId = getLogicalId(usersHandler.node.defaultChild as software.amazon.awscdk.services.lambda.CfnFunction)
        val envFile = java.io.File("users-awsenv.json")
        val envContent = """
{
  "$handlerLogicalId": {
    "DYNAMODB_TABLE_NAME": "${usersTable.tableName}",
    "COGNITO_USER_POOL_ID": "${userPool.userPoolId}",
    "LAMBDA_FUNCTION_NAME": "${usersHandler.functionName}"
  }
}
"""
        envFile.writeText(envContent.trim())
    }
}
