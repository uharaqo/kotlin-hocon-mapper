package com.github.uharaqo.hocon.mapper

import com.typesafe.config.ConfigFactory
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DeserializationEdgeCaseTest {

    @Nested
    inner class ShouldSuccess {
        @Test
        fun `null can be injected when explicitly defined`() {
            // given
            @Serializable
            data class OptionalValue(val optValue: String?)

            val config = ConfigFactory.parseString("""{"optValue": null}""")

            // when
            val result = OptionalValue.serializer().load(config)

            // then
            result.optValue shouldBe null

            // when
            val emptyConfig = ConfigFactory.parseString("{}")

            // then
            shouldThrow<MissingFieldException> { OptionalValue.serializer().load(emptyConfig) }
        }

        @Test
        fun `unknown keys are ignored`() {
            // given
            val config = ConfigFactory.parseString("""{"string": "foo", "unknown": 123}""")

            // when
            val result = BasicModels.SimpleObj.serializer().load(config)

            // then
            result shouldBe BasicModels.SimpleObj("foo")
        }
    }

    @Nested
    inner class ShouldThrowException {
        @Test
        fun `throw MissingFieldException on loading missing key`() {
            // given
            val config = ConfigFactory.parseString("{}")

            // when / then
            shouldThrow<MissingFieldException> { BasicModels.SimpleObj.serializer().load(config) }
        }

        @Test
        fun `throw MissingFieldException on loading missing key even for nullable property`() {
            // given
            @Serializable
            data class OptionalValue(val optValue: String?)

            val config = ConfigFactory.parseString("""{"optValue": null}""")

            // when
            val emptyConfig = ConfigFactory.parseString("{}")

            // then
            shouldThrow<MissingFieldException> { OptionalValue.serializer().load(emptyConfig) }
        }

        @Test
        fun `default value is not supported for deserialization`() {
            // given
            @Serializable
            data class OptionalValue(val optValue: String = "default")

            val config = ConfigFactory.parseString("{}")

            // when // then
            shouldThrow<MissingFieldException> { OptionalValue.serializer().load(config) }
        }

        @Test
        fun `invalid type`() {
            // given
            @Serializable
            data class IntValue(val int: Int)

            val config = ConfigFactory.parseString("{ int: text }")

            // when // then
            shouldThrow<SerializationException> { IntValue.serializer().load(config) }
        }
    }
}