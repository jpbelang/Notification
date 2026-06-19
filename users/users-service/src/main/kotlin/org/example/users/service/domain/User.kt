package org.example.users.service.domain

data class User(
    val id: TypedUUID<User>,
    val name: String,
    val email: String
)
