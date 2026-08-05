package ca.notification.users.service.adapter.persistence

import ca.notification.users.service.domain.*
import ca.notification.users.service.port.outbound.CredentialsRepository
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

class CognitoCredentialsRepository(
    private val cognitoClient: CognitoIdentityProviderClient,
    private val userPoolId: String,
    private val userPoolClientId: String
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

    override fun authenticate(email: String, password: String): AuthTokens? {
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
            val authResult = response.authenticationResult()
            if (authResult != null) {
                AuthTokens(
                    accessToken = authResult.accessToken(),
                    idToken = authResult.idToken(),
                    refreshToken = authResult.refreshToken(),
                    expiresIn = authResult.expiresIn()
                )
            } else {
                null
            }
        } catch (e: NotAuthorizedException) {
            null
        } catch (e: UserNotFoundException) {
            null
        } catch (e: Exception) {
            null
        }
    }
}
