package ca.notification.users.service.micronaut

import ca.notification.users.service.adapter.persistence.InMemoryCredentialsRepository
import ca.notification.users.service.adapter.persistence.InMemoryUserRepository
import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import ca.notification.users.service.adapter.persistence.UserCredentials
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.micronaut.context.ApplicationContext
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import io.micronaut.json.JsonMapper
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest

@MicronautTest
class UserAuthenticationTest(
    private val applicationContext: ApplicationContext,
    private val jsonMapper: JsonMapper,
    private val userRepository: InMemoryUserRepository,
    private val credentialsRepository: InMemoryCredentialsRepository
) : StringSpec({

    val handler = ApiGatewayProxyRequestEventFunction(applicationContext)

    "should authenticate user with correct credentials" {
        val userId = TypedUUID.create<User>()
        val email = "auth@example.com"
        val password = "correct-password"
        
        userRepository.save(User.from(userId, "Auth User", email, "123"))
        
        // Use reflection or a test helper if needed, but since it's InMemory, 
        // I can use the methods I know exist or just use save (but save checks if exists)
        // Actually I'll just use the public save method of the repository if I can
        // Wait, the InMemoryCredentialsRepository.save takes NewUser.
        
        // Better: just use the API to create the user first
        val createBody = mapOf(
            "name" to "Auth User",
            "email" to email,
            "phoneNumber" to "123",
            "password" to password
        )
        handler.handleRequest(APIGatewayProxyRequestEvent().apply {
            httpMethod = "POST"
            path = "/users"
            body = jsonMapper.writeValueAsString(createBody)
        }, null)

        val authBody = mapOf("password" to password)
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "POST"
            this.path = "/users/email/$email/authenticate"
            this.body = jsonMapper.writeValueAsString(authBody)
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 200
        val responseBody = response.body ?: ""
        responseBody shouldContain "Auth User"
        responseBody shouldContain email
    }

    "should return 401 for incorrect password" {
        val email = "wrong-pass@example.com"
        val password = "secret-password"
        
        val createBody = mapOf(
            "name" to "Wrong Pass User",
            "email" to email,
            "phoneNumber" to "123",
            "password" to password
        )
        handler.handleRequest(APIGatewayProxyRequestEvent().apply {
            httpMethod = "POST"
            path = "/users"
            body = jsonMapper.writeValueAsString(createBody)
        }, null)

        val authBody = mapOf("password" to "wrong-password")
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "POST"
            this.path = "/users/email/$email/authenticate"
            this.body = jsonMapper.writeValueAsString(authBody)
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 401
    }

    "should return 401 for non-existent user" {
        val authBody = mapOf("password" to "any-password")
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "POST"
            this.path = "/users/email/nonexistent@example.com/authenticate"
            this.body = jsonMapper.writeValueAsString(authBody)
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 401
    }
})
