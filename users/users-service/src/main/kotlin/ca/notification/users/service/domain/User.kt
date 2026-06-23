package ca.notification.users.service.domain

data class NewUser(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val password: String
)

data class User(
    val id: TypedUUID<User>,
    val name: String,
    val email: String,
    val phoneNumber: String
) {
    companion object {
        fun createNew(
            name: String,
            email: String,
            phoneNumber: String,
            password: String
        ) = NewUser(name, email, phoneNumber, password)

        fun from(
            id: TypedUUID<User>,
            name: String,
            email: String,
            phoneNumber: String
        ) = User(id, name, email, phoneNumber)

        fun withId(newUser: NewUser, id: TypedUUID<User>) = User(
            id = id,
            name = newUser.name,
            email = newUser.email,
            phoneNumber = newUser.phoneNumber
        )
    }
}
