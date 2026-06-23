package ca.notification.users.service.delivery.lambda

import ca.notification.users.service.domain.TypedUUID
import ca.notification.users.service.port.inbound.CreateUserUseCase
import ca.notification.users.service.port.inbound.DeleteUserUseCase
import ca.notification.users.service.port.inbound.FindUserUseCase
import ca.notification.users.service.port.inbound.UpdateUserUseCase
import org.slf4j.LoggerFactory

class UserHandler(
    private val createUserUseCase: CreateUserUseCase,
    private val findUserUseCase: FindUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) {

    private val logger = LoggerFactory.getLogger(UserHandler::class.java)

    fun create(request: CreateUserRequest): UserResponse {

        System.getenv().forEach { (key, value) ->
            logger.info("$key = $value")
        }

        val user = createUserUseCase.execute(
            CreateUserUseCase.Command(
                request.name,
                request.email,
                request.phoneNumber,
                request.password
            )
        )

        return UserResponse(
            id = user.id.toString(),
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber
        )
    }

    fun update(id: String, request: UpdateUserRequest): UserResponse {
        val user = updateUserUseCase.execute(
            UpdateUserUseCase.Command(
                id = TypedUUID.fromString(id),
                name = request.name,
                email = request.email,
                phoneNumber = request.phoneNumber
            )
        )

        return UserResponse(
            id = user.id.toString(),
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber
        )
    }

    fun getById(id: String): UserResponse? {
        val user = findUserUseCase.findById(TypedUUID.fromString(id))
        return user?.let {
            UserResponse(
                id = it.id.toString(),
                name = it.name,
                email = it.email,
                phoneNumber = it.phoneNumber
            )
        }
    }

    fun getByEmail(email: String): UserResponse? {
        val user = findUserUseCase.findByEmail(email)
        return user?.let {
            UserResponse(
                id = it.id.toString(),
                name = it.name,
                email = it.email,
                phoneNumber = it.phoneNumber
            )
        }
    }

    fun delete(id: String) {
        deleteUserUseCase.execute(TypedUUID.fromString(id))
    }
}
