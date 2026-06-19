package ca.notification.users.service.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.UUID

class TypedUUIDTest : StringSpec({

    "create() should generate a valid TypedUUID" {
        val typedUUID = TypedUUID.create<Any>()
        typedUUID shouldNotBe null
        typedUUID.asUUID() shouldNotBe null
    }

    "fromString() should correctly parse a valid UUID string" {
        val uuidString = "550e8400-e29b-41d4-a716-446655440000"
        val typedUUID = TypedUUID.fromString<Any>(uuidString)
        typedUUID.asString() shouldBe uuidString
        typedUUID.asUUID() shouldBe UUID.fromString(uuidString)
    }

    "fromString() should throw exception for invalid UUID string" {
        shouldThrow<IllegalArgumentException> {
            TypedUUID.fromString<Any>("invalid-uuid")
        }
    }

    "equals() and hashCode() should work correctly" {
        val uuidString = "550e8400-e29b-41d4-a716-446655440000"
        val uuid1 = TypedUUID.fromString<Any>(uuidString)
        val uuid2 = TypedUUID.fromString<Any>(uuidString)
        val uuid3 = TypedUUID.create<Any>()

        uuid1 shouldBe uuid2
        uuid1.hashCode() shouldBe uuid2.hashCode()
        
        uuid1 shouldNotBe uuid3
        uuid1.hashCode() shouldNotBe uuid3.hashCode()
    }

    "toString() should return the same as asString()" {
        val typedUUID = TypedUUID.create<Any>()
        typedUUID.toString() shouldBe typedUUID.asString()
    }
    
    "different types with same UUID should be equal" {
        val uuidString = UUID.randomUUID().toString()
        val uuidA = TypedUUID.fromString<String>(uuidString)
        val uuidB = TypedUUID.fromString<Int>(uuidString)
        
        uuidA shouldBe uuidB
    }
})
