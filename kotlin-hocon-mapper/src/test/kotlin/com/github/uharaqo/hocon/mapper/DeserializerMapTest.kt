package com.github.uharaqo.hocon.mapper

import com.typesafe.config.ConfigFactory
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

class DeserializerMapTest {

    private enum class DataEnum { ELEMENT }

    @Test
    fun `map should be deserialized correctly`() {
        // given

        @Serializable
        data class PrimitiveLists(
            val char: List<Char>,
            val string: List<String>,
            val bool: List<Boolean>,
            val byte: List<Byte>,
            val int: List<Int>,
            val long: List<Long>,
            val short: List<Short>,
            val float: List<Float>,
            val double: List<Double>,
            val enum: List<DataEnum>,
            val map: Map<String, String>
        )

        @Serializable
        data class PrimitiveListsMap(
            val map: Map<String, PrimitiveLists>
        )

        val config = ConfigFactory.parseString(
            """
            | {
            |   map: {
            |     123: {
            |       char: [a]
            |       string: [abc]
            |       bool: [true]
            |       byte: [1]
            |       int: [${Int.MAX_VALUE}]
            |       long: [${Long.MAX_VALUE}]
            |       short: [${Short.MAX_VALUE}]
            |       float: [${Float.MAX_VALUE}]
            |       double: [${Double.MAX_VALUE}]
            |       enum: [${DataEnum.ELEMENT}]
            |       map: {
            |         key: val
            |       }
            |     }
            |   }
            | }
            """.trimMargin()
        )

        // when
        val result = PrimitiveListsMap.serializer().load(config)

        // then
        val nullableMap = result.map["123"]
        nullableMap shouldNotBe null

        val map = nullableMap!!
        map.char[0] shouldBe 'a'
        map.string[0] shouldBe "abc"
        map.bool[0] shouldBe true
        map.byte[0] shouldBe 1.toByte()
        map.int[0] shouldBe Int.MAX_VALUE
        map.long[0] shouldBe Long.MAX_VALUE
        map.short[0] shouldBe Short.MAX_VALUE
        map.float[0] shouldBe Float.MAX_VALUE
        map.double[0] shouldBe Double.MAX_VALUE
        map.enum[0] shouldBe DataEnum.ELEMENT
        map.map.entries.first() should { e ->
            e.key shouldBe "key"
            e.value shouldBe "val"
        }
    }
}
