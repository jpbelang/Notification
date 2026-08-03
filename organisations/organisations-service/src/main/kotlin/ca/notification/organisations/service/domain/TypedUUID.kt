package ca.notification.organisations.service.domain

import java.util.UUID

class TypedUUID<T> private constructor(val uuid: UUID) {

    companion object {
        fun <T>fromString(value: String): TypedUUID<T> {
            return TypedUUID<T>(UUID.fromString(value))
        }

        fun <T>create(): TypedUUID<T> {
            return TypedUUID<T>(UUID.randomUUID())
        }
    }

    fun asString(): String = this.uuid.toString()
    fun asUUID(): UUID = this.uuid

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TypedUUID<*>

        return uuid == other.uuid
    }

    override fun toString() = asString()

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}
