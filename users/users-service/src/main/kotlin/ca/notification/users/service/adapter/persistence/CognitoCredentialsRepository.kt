package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.NewUser
import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.domain.UserExistsException
import ca.notification.users.service.port.outbound.CredentialsRepository
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException

@Singleton
@Requires(property = "micronaut.environment", value = "lambda")
class CognitoCredentialsRepository(
    private val cognitoClient: CognitoIdentityProviderClient,
    @Property(name = "cognito.user-pool-id") private val userPoolId: String,
    @Property(name = "cognito.user-pool-client-id") private val userPoolClientId: String
) : CredentialsRepository {

    override fun save(user: NewUser): TypedUUID<User> {
        val request = AdminCreateUserRequest.builder()
            .userPoolId(userPoolId)
            .username(user.email)
            .temporaryPassword(user.password)
            .userAttributes(
                AttributeType.builder().name("email").value(user.email).build(),
                AttributeType.builder().name("email_verified").value("true").build(),
                AttributeType.builder().name("name").value(user.name).build(),
                AttributeType.builder().name("phone_number").value(user.phoneNumber).build()
            )
            .messageAction("SUPPRESS")
            .build()

        try {
            val response = cognitoClient.adminCreateUser(request)
            val sub = response.user().attributes().first { it.name() == "sub" }.value()
            return TypedUUID.fromString(sub)
        } catch (e: UsernameExistsException) {
            throw UserExistsException("User with email ${user.email} already exists")
        }
    }

    override fun update(user: User) {
        val request = AdminUpdateUserAttributesRequest.builder()
            .userPoolId(userPoolId)
            .username(user.id.toString())
            .userAttributes(
                AttributeType.builder().name("email").value(user.email).build(),
                AttributeType.builder().name("email_verified").value("true").build(),
                AttributeType.builder().name("name").value(user.name).build(),
                AttributeType.builder().name("phone_number").value(user.phoneNumber).build()
            )
            .build()

        try {
            cognitoClient.adminUpdateUserAttributes(request)
        } catch (e: UsernameExistsException) {
            throw UserExistsException("User with email ${user.email} already exists")
        }
    }

    override fun delete(id: TypedUUID<User>) {
        val request = AdminDeleteUserRequest.builder()
            .userPoolId(userPoolId)
            .username(id.toString())
            .build()

        cognitoClient.adminDeleteUser(request)
    }

    override fun authenticate(email: String, password: String): Boolean {
        val request = AdminInitiateAuthRequest.builder()
            .userPoolId(userPoolId)
            .clientId(userPoolClientId)
            .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
            .authParameters(mapOf(
                "USERNAME" to email,
                "PASSWORD" to password
            ))
            .build()

        return try {
            val response = cognitoClient.adminInitiateAuth(request)
            response.authenticationResult() != null
        } catch (e: NotAuthorizedException) {
            false
        } catch (e: UserNotFoundException) {
            false
        } catch (e: Exception) {
            false
        }
    }
}
