package ca.notification.users.service.micronaut

import ca.notification.users.service.adapter.persistence.InMemoryCredentialsRepository
import ca.notification.users.service.adapter.persistence.InMemoryUserRepository
import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.domain.User
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.micronaut.context.ApplicationContext
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import io.micronaut.json.JsonMapper
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import kotlin.collections.get

@MicronautTest
class UserLambdaTest(
    private val applicationContext: ApplicationContext,
    private val jsonMapper: JsonMapper,
    private val userRepository: InMemoryUserRepository,
    private val credentialsRepository: InMemoryCredentialsRepository
) : StringSpec({

    val handler = ApiGatewayProxyRequestEventFunction(applicationContext)

    "should create a user and return 201" {
        val body = mapOf(
            "name" to "John Doe",
            "email" to "john@example.com",
            "phoneNumber" to "555-1234",
            "password" to "secret"
        )
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "POST"
            this.path = "/users"
            this.body = jsonMapper.writeValueAsString(body)
        }

        val response = handler.handleRequest(request, null)
        val responseBody = response.body ?: ""

        response.statusCode shouldBe 201
        responseBody shouldContain "id"
        responseBody shouldContain "John Doe"
        responseBody shouldContain "john@example.com"
        responseBody shouldContain "555-1234"
        responseBody shouldNotContain "secret"
        responseBody shouldNotContain "password"

        // Further validation: parse and check fields
        val parsedResponse = jsonMapper.readValue(responseBody, Map::class.java)
        parsedResponse["name"] shouldBe "John Doe"
        parsedResponse["email"] shouldBe "john@example.com"
        parsedResponse["phoneNumber"] shouldBe "555-1234"
        parsedResponse["id"] shouldNotBe null
        parsedResponse.containsKey("password") shouldBe false

        // Verify user was saved in repository without password
        val savedUserId = TypedUUID.fromString<User>(parsedResponse["id"] as String)
        val savedUser = userRepository.findById(savedUserId)
        savedUser shouldNotBe null
        savedUser?.name shouldBe "John Doe"
        savedUser?.email shouldBe "john@example.com"
        savedUser?.phoneNumber shouldBe "555-1234"

        // Verify credentials were saved separately with password
        val savedCredentials = credentialsRepository.findByUserId(savedUserId)
        savedCredentials shouldNotBe null
        savedCredentials?.email shouldBe "john@example.com"
        savedCredentials?.password shouldBe "secret"
    }

    "should return error if email already exists" {
        val body = mapOf(
            "name" to "Duplicate",
            "email" to "duplicate@example.com",
            "phoneNumber" to "000",
            "password" to "pass"
        )
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "POST"
            this.path = "/users"
            this.body = jsonMapper.writeValueAsString(body)
        }

        handler.handleRequest(request, null).statusCode shouldBe 201
        
        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 409
    }

    "should get user by id" {
        val user = User.from(TypedUUID.create(), "Finder", "finder@example.com", "999")
        userRepository.save(user)

        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "GET"
            this.path = "/users/${user.id}"
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 200
        val responseBody = response.body ?: ""
        responseBody shouldContain "Finder"
        responseBody shouldContain "finder@example.com"
    }

    "should update user" {
        val user = User.from(TypedUUID.create(), "Original", "original@example.com", "111")
        userRepository.save(user)

        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "PUT"
            this.path = "/users/${user.id}"
            this.body = """
                {
                    "name": "Updated",
                    "email": "original@example.com",
                    "phoneNumber": "222"
                }
            """.trimIndent()
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 200
        response.body shouldContain "Updated"
        response.body shouldContain "222"

        val updated = userRepository.findById(user.id)
        updated?.name shouldBe "Updated"
    }

    "should update user with email change" {
        val user = User.from(TypedUUID.create(), "Original", "old-email@example.com", "111")
        userRepository.save(user)

        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "PUT"
            this.path = "/users/${user.id}"
            this.body = """
                {
                    "name": "Original",
                    "email": "new-email@example.com",
                    "phoneNumber": "111"
                }
            """.trimIndent()
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 200

        userRepository.findByEmail("old-email@example.com") shouldBe null
        userRepository.findByEmail("new-email@example.com")?.id shouldBe user.id
    }

    "should return 404 when updating non-existent user" {
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "PUT"
            this.path = "/users/${TypedUUID.create<User>()}"
            this.body = """
                {
                    "name": "Doesnt Matter",
                    "email": "any@example.com",
                    "phoneNumber": "000"
                }
            """.trimIndent()
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 404
    }

    "should get user by email" {
        val user = User.from(TypedUUID.create(), "EmailFinder", "email@example.com", "888")
        userRepository.save(user)

        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "GET"
            this.path = "/users/email/${user.email}"
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 200
        val responseBody = response.body ?: ""
        responseBody shouldContain "EmailFinder"
        responseBody shouldContain "email@example.com"
    }

    "should return 404 if user not found" {
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "GET"
            this.path = "/users/${TypedUUID.create<User>()}"
        }

        handler.handleRequest(request, null).statusCode shouldBe 404

        val emailRequest = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "GET"
            this.path = "/users/email/notfound@example.com"
        }
        handler.handleRequest(emailRequest, null).statusCode shouldBe 404
    }

    "should return 404 for unknown path" {
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "GET"
            this.path = "/unknown"
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 404
    }

    "should return 405 for unknown method?" {
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "GET"
            this.path = "/users"
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 405
    }

    "should delete user" {
        val user = User.from(TypedUUID.create(), "Delete Me", "delete@example.com", "000")
        userRepository.save(user)

        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "DELETE"
            this.path = "/users/${user.id}"
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 204

        userRepository.findById(user.id) shouldBe null
    }

    "should return 404 when deleting non-existent user" {
        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "DELETE"
            this.path = "/users/${TypedUUID.create<User>()}"
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 404
    }

})