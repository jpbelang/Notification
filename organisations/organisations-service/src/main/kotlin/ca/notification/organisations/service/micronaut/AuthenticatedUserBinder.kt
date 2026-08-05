package ca.notification.organisations.service.micronaut

import ca.notification.organisations.service.domain.AuthenticatedUser
import io.micronaut.core.bind.ArgumentBinder
import io.micronaut.core.convert.ArgumentConversionContext
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder
import io.micronaut.serde.ObjectMapper
import jakarta.inject.Singleton
import java.util.*

@Singleton
class AuthenticatedUserBinder(private val objectMapper: ObjectMapper) : TypedRequestArgumentBinder<AuthenticatedUser> {

    override fun argumentType(): Argument<AuthenticatedUser> {
        return Argument.of(AuthenticatedUser::class.java)
    }

    override fun bind(context: ArgumentConversionContext<AuthenticatedUser>, source: HttpRequest<*>): ArgumentBinder.BindingResult<AuthenticatedUser> {
        val idToken = source.cookies.get("idToken")
        if (idToken != null) {
            try {
                val parts = idToken.value.split(".")
                if (parts.size >= 2) {
                    val payload = parts[1]
                    val decoded = Base64.getUrlDecoder().decode(payload)
                    val claims = objectMapper.readValue(decoded, Map::class.java)
                    
                    val sub = claims["sub"] as? String
                    val email = claims["email"] as? String
                    val name = claims["name"] as? String
                    
                    if (sub != null && email != null) {
                        val user = AuthenticatedUser(
                            id = sub,
                            email = email,
                            name = name
                        )
                        return object : ArgumentBinder.BindingResult<AuthenticatedUser> {
                            override fun getValue(): Optional<AuthenticatedUser> = Optional.of(user)
                            override fun isSatisfied(): Boolean = true
                        }
                    }
                }
            } catch (e: Exception) {
                // In a real app, we would log this
            }
        }
        
        return object : ArgumentBinder.BindingResult<AuthenticatedUser> {
            override fun getValue(): Optional<AuthenticatedUser> = Optional.empty()
            override fun isSatisfied(): Boolean = true
        }
    }
}
