package com.github.uharaqo.hocon.mapper

import com.typesafe.config.ConfigFactory
import io.kotlintest.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class SerializerTest {

    @Test
    fun `if any, default value is used for serializing missing key`() {
        // given
        @Serializable
        data class OptionalValue(val optValue: String = "default")

        val json = "{}"

        // when
        val result = Json.decodeFromString(OptionalValue.serializer(), json)

        // then
        result.optValue shouldBe "default"
    }

    @Suppress("BooleanLiteralArgument")
    @Test
    fun `StringBooleanSerializer deserialization works as expected`() {
        // given
        @Serializable
        data class Data(
            @Serializable(with = StringBooleanSerializer::class) val bool1: Boolean,
            @Serializable(with = StringBooleanSerializer::class) val bool2: Boolean,
            @Serializable(with = StringBooleanSerializer::class) val bool3: Boolean,
            @Serializable(with = StringBooleanSerializer::class) val bool4: Boolean,
            @Serializable(with = StringBooleanSerializer::class) val bool5: Boolean,
            @Serializable(with = StringBooleanSerializer::class) val bool6: Boolean
        )

        val config = ConfigFactory.parseString(
            """
            |{
            |  bool1: "true"
            |  bool2: "on"
            |  bool3: "yes"
            |  bool4: "false"
            |  bool5: "off"
            |  bool6: "no"
            |}
            """.trimMargin()
        )

        // when
        val result = Data.serializer().load(config)

        // then
        result shouldBe Data(
            true, true, true,
            false, false, false
        )
    }
}