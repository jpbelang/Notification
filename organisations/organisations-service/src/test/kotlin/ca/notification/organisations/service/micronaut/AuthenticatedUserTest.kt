package ca.notification.organisations.service.micronaut

import ca.notification.organisations.service.domain.AuthenticatedUser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.micronaut.context.ApplicationContext
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.json.JsonMapper
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction
import java.util.*

@MicronautTest
class AuthenticatedUserTest(
    private val applicationContext: ApplicationContext,
    private val jsonMapper: JsonMapper
) : StringSpec({

    val handler = ApiGatewayProxyRequestEventFunction(applicationContext)

    "should extract user from idToken cookie" {
        val payload = mapOf(
            "sub" to "user-123",
            "email" to "test@example.com",
            "name" to "Test User"
        )
        val payloadJson = jsonMapper.writeValueAsString(payload)
        val encodedPayload = Base64.getUrlEncoder().encodeToString(payloadJson.toByteArray())
        val dummyToken = "header.$encodedPayload.signature"

        val request = APIGatewayProxyRequestEvent().apply {
            this.httpMethod = "GET"
            this.path = "/test-auth"
            this.headers = mapOf("cookie" to "idToken=$dummyToken")
        }

        val response = handler.handleRequest(request, null)
        response.statusCode shouldBe 200
        
        val responseBody = response.body
        responseBody shouldNotBe null
        
        val user = jsonMapper.readValue(responseBody, AuthenticatedUser::class.java)
        user.id shouldBe "user-123"
        user.email shouldBe "test@example.com"
        user.name shouldBe "Test User"
    }
})

@Controller("/test-auth")
class TestAuthController {
    @Get("/")
    fun test(user: AuthenticatedUser): AuthenticatedUser = user
}
