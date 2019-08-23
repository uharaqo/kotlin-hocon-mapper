package com.github.uharaqo.hocon.mapper

import com.typesafe.config.ConfigFactory
import io.kotlintest.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.decode
import org.junit.jupiter.api.Test

class DeserializerListTest {

    private enum class DataEnum { ELEMENT }

    @Serializable
    private data class PrimitiveLists(
        val char: List<Char>,
        val string: List<String>,
        val bool: List<Boolean>,
        val byte: List<Byte>,
        val int: List<Int>,
        val long: List<Long>,
        val short: List<Short>,
        val float: List<Float>,
        val double: List<Double>,
        val enum: List<DataEnum>
    )

    @Test
    fun `list should be deserialized correctly`() {
        // given
        val config = ConfigFactory.parseString(
            """
            | {
            |    char: [a]
            |    string: [abc]
            |    bool: [true]
            |    byte: [1]
            |    int: [${Int.MAX_VALUE}]
            |    long: [${Long.MAX_VALUE}]
            |    short: [${Short.MAX_VALUE}]
            |    float: [${Float.MAX_VALUE}]
            |    double: [${Double.MAX_VALUE}]
            |    enum: [${DataEnum.ELEMENT}]
            | }
            """.trimMargin()
        )

        // when
        val result = ConfigDecoder(config).decode(PrimitiveLists.serializer())

        // then
        result.char[0] shouldBe 'a'
        result.string[0] shouldBe "abc"
        result.bool[0] shouldBe true
        result.byte[0] shouldBe 1.toByte()
        result.int[0] shouldBe Int.MAX_VALUE
        result.long[0] shouldBe Long.MAX_VALUE
        result.short[0] shouldBe Short.MAX_VALUE
        result.float[0] shouldBe Float.MAX_VALUE
        result.double[0] shouldBe Double.MAX_VALUE
        result.enum[0] shouldBe DataEnum.ELEMENT
    }

    @Test
    fun `list of lists`() {

        @Serializable
        data class NestedList(val ints: List<List<Int>>)

        // given
        val config = ConfigFactory.parseString(
            """
            | {
            |    ints: [
            |      [${Int.MIN_VALUE}],
            |      [${Int.MAX_VALUE}]
            |    ]
            | }
            """.trimMargin()
        )

        // when
        val result = ConfigDecoder(config).decode(NestedList.serializer())

        // then
        result.ints[0][0] shouldBe Int.MIN_VALUE
        result.ints[1][0] shouldBe Int.MAX_VALUE
    }

    @Test
    fun `list of objects`() {

        @Serializable
        data class IntValue(val int: Int)

        @Serializable
        data class NestedList(val ints: List<IntValue>)

        // given
        val config = ConfigFactory.parseString(
            """
            | {
            |    ints: [
            |      {int: ${Int.MAX_VALUE}},
            |      {int: ${Int.MIN_VALUE}}
            |    ]
            | }
            """.trimMargin()
        )

        // when
        val result = ConfigDecoder(config).decode(NestedList.serializer())

        // then
        result.ints[0].int shouldBe Int.MAX_VALUE
        result.ints[1].int shouldBe Int.MIN_VALUE
    }
}