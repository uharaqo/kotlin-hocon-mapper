package com.github.uharaqo.hocon.mapper

import com.typesafe.config.ConfigFactory
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class DeserializerBasicTest {

    private enum class DataEnum { ELEMENT }

    @Test
    fun `primitives should be decoded correctly`() {
        // given
        @Serializable
        data class Primitives(
            val char: Char,
            val string: String,
            val bool: Boolean,
            val byte: Byte,
            val int: Int,
            val long: Long,
            val short: Short,
            val float: Float,
            val double: Double,
            val enum: DataEnum
        )

        val config = ConfigFactory.parseString(
            """
            | {
            |    char: a
            |    string: abc
            |    bool: true
            |    byte: 1
            |    int: ${Int.MAX_VALUE}
            |    long: ${Long.MAX_VALUE}
            |    short: ${Short.MAX_VALUE}
            |    float: ${Float.MAX_VALUE}
            |    double: ${Double.MAX_VALUE}
            |    enum: ${DataEnum.ELEMENT}
            | }
            """.trimMargin()
        )

        // when
        val result = Primitives.serializer().load(config)

        // then
        result.char shouldBe 'a'
        result.string shouldBe "abc"
        result.bool shouldBe true
        result.byte shouldBe 1.toByte()
        result.int shouldBe Int.MAX_VALUE
        result.long shouldBe Long.MAX_VALUE
        result.short shouldBe Short.MAX_VALUE
        result.float shouldBe Float.MAX_VALUE
        result.double shouldBe Double.MAX_VALUE
        result.enum shouldBe DataEnum.ELEMENT
    }

    @Test
    fun `nested objects can be parsed`() {
        // given

        @Serializable
        data class IntVal(val int: Int)

        @Serializable
        data class ObjInObj(val intVal: IntVal)

        val config = ConfigFactory.parseString("""{intVal: {int: ${Int.MAX_VALUE}}}""")

        // when
        val result = ObjInObj.serializer().load(config)

        // then
        result.intVal.int shouldBe Int.MAX_VALUE
    }

    @Test
    fun `MissingFieldException on missing key`() {
        // given

        @Serializable
        data class Simple(
            val string: String
        )

        val config = ConfigFactory.parseString("{}")

        // when / then
        shouldThrow<MissingFieldException> {
            Simple.serializer().load(config)
        }
    }

    @Test
    fun `MissingFieldException is thrown on missing key even for a nullable field`() {
        // given

        @Serializable
        data class OptionalValue(val optValue: String?)

        val json = "{}"

        // when / then
        shouldThrow<MissingFieldException> {
            Json.parse(OptionalValue.serializer(), json)
        }
    }

    @Test
    fun `default value is used for missing key`() {
        // given

        @Serializable
        data class OptionalValue(val optValue: String = "default")

        val json = "{}"

        // when
        val result = Json.parse(OptionalValue.serializer(), json)

        // then
        result.optValue shouldBe "default"
    }

    @Test
    fun `unknown keys are ignored`() {
        // given
        @Serializable
        data class Simple(
            val string: String
        )

        val config = ConfigFactory.parseString("""{"string": "abc", "unknown": 123}""")

        // when
        val result = Simple.serializer().load(config)

        // then
        result.string shouldBe "abc"
    }

    @Test
    fun `nested objects can be deserialized`() {
        // given
        @Serializable
        data class C(val bool: Boolean)

        @Serializable
        data class B(val c: C)

        @Serializable
        data class A(val b: B)

        val config = ConfigFactory.parseString("""{b: {c: {bool: true}}}""")

        // when
        val result = A.serializer().load(config)

        // then
        result.b.c.bool shouldBe true
    }
}
