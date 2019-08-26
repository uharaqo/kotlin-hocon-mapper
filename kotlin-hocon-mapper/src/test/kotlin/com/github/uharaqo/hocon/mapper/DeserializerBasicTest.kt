@file:UseSerializers(
    PeriodSerializer::class,
    DurationSerializer::class,
    ConfigMemorySizeSerializer::class
)

package com.github.uharaqo.hocon.mapper

import com.github.uharaqo.hocon.mapper.DeserializerBasicTest.AdditionalModels.Durations
import com.github.uharaqo.hocon.mapper.DeserializerBasicTest.AdditionalModels.MemSizes
import com.github.uharaqo.hocon.mapper.DeserializerBasicTest.AdditionalModels.Periods
import com.github.uharaqo.hocon.mapper.DeserializerBasicTest.BasicModels.BasicTypes
import com.github.uharaqo.hocon.mapper.DeserializerBasicTest.BasicModels.SampleEnum
import com.github.uharaqo.hocon.mapper.DeserializerBasicTest.BasicModels.SimpleObj
import com.github.uharaqo.hocon.mapper.DeserializerBasicTest.CollectionModels.BasicTypeLists
import com.github.uharaqo.hocon.mapper.DeserializerBasicTest.CollectionModels.NestedList
import com.github.uharaqo.hocon.mapper.DeserializerBasicTest.CollectionModels.ObjectList
import com.github.uharaqo.hocon.mapper.DeserializerBasicTest.CollectionModels.ObjectListMap
import com.github.uharaqo.hocon.mapper.DeserializerBasicTest.NestedModels.Inner
import com.github.uharaqo.hocon.mapper.DeserializerBasicTest.NestedModels.Outer
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigMemorySize
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Duration
import java.time.Period
import java.util.stream.Stream
import kotlin.math.pow

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeserializerBasicTest {

    private interface BasicModels {

        enum class SampleEnum { ELEMENT }

        @Serializable
        data class SimpleObj(val string: String)

        @Serializable
        data class BasicTypes(
            val char: Char, val string: String, val bool: Boolean,
            val byte: Byte, val int: Int, val long: Long,
            val short: Short, val float: Float, val double: Double,
            val enum: SampleEnum
        )
    }

    private interface NestedModels {
        @Serializable
        data class Inner(val obj: SimpleObj)

        @Serializable
        data class Outer(val inner: Inner)
    }

    private interface CollectionModels {

        @Serializable
        data class BasicTypeLists(
            val char: List<Char>,
            val string: List<String>,
            val bool: List<Boolean>,
            val byte: List<Byte>,
            val int: List<Int>,
            val long: List<Long>,
            val short: List<Short>,
            val float: List<Float>,
            val double: List<Double>,
            val enum: List<SampleEnum>
        )

        @Serializable
        data class NestedList(val ints: List<List<Int>>)

        @Serializable
        data class ObjectList(val objects: List<SimpleObj>)

        @Serializable
        data class ObjectListMap(
            val map: Map<String, ObjectList>
        )
    }

    interface AdditionalModels {

        @Serializable
        data class Periods(
            val period1: Period, val period2: Period, val period3: Period, val period4: Period
        )

        @Serializable
        data class Durations(
            val duration1: Duration, val duration2: Duration, val duration3: Duration,
            val duration4: Duration, val duration5: Duration, val duration6: Duration,
            val duration7: Duration
        )

        @Serializable
        data class MemSizes(
            val mem1: ConfigMemorySize, val mem2: ConfigMemorySize, val mem3: ConfigMemorySize,
            val mem4: ConfigMemorySize, val mem5: ConfigMemorySize, val mem6: ConfigMemorySize,
            val mem7: ConfigMemorySize
        )
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class SerializerTests {

        private fun dataProvider() = Stream.of(
            arguments(
                "basic types",
                BasicTypes.serializer(),
                BasicTypes(
                    'a', "abc", true,
                    1.toByte(), Int.MAX_VALUE, Long.MAX_VALUE,
                    Short.MAX_VALUE, Float.MAX_VALUE, Double.MAX_VALUE,
                    SampleEnum.ELEMENT
                ),
                """{
                |  char: a,
                |  string: abc,
                |  bool: true,
                |  byte: 1,
                |  int: ${Int.MAX_VALUE},
                |  long: ${Long.MAX_VALUE},
                |  short: ${Short.MAX_VALUE},
                |  float: ${Float.MAX_VALUE},
                |  double: ${Double.MAX_VALUE},
                |  enum: ${SampleEnum.ELEMENT}
                |}
                """.trimMargin()
            ),

            arguments(
                "nested objects",
                Outer.serializer(),
                Outer(Inner(SimpleObj("foo"))),
                """{
                |  inner: {
                |    obj: {
                |      string: foo
                |    }
                |  }
                |}""".trimMargin()
            ),

            arguments(
                "lists of basic types",
                BasicTypeLists.serializer(),
                BasicTypeLists(
                    listOf('a'),
                    listOf("abc"),
                    listOf(true),
                    listOf(1.toByte()),
                    listOf(Int.MAX_VALUE),
                    listOf(Long.MAX_VALUE),
                    listOf(Short.MAX_VALUE),
                    listOf(Float.MAX_VALUE),
                    listOf(Double.MAX_VALUE),
                    listOf(SampleEnum.ELEMENT)
                ),
                """{
                |  char: [
                |    a
                |  ],
                |  string: [
                |    abc
                |  ],
                |  bool: [
                |    true
                |  ],
                |  byte: [
                |    1
                |  ],
                |  int: [
                |    ${Int.MAX_VALUE}
                |  ],
                |  long: [
                |    ${Long.MAX_VALUE}
                |  ],
                |  short: [
                |    ${Short.MAX_VALUE}
                |  ],
                |  float: [
                |    ${Float.MAX_VALUE}
                |  ],
                |  double: [
                |    ${Double.MAX_VALUE}
                |  ],
                |  enum: [
                |    ${SampleEnum.ELEMENT}
                |  ]
                |}
                """.trimMargin()
            ),

            arguments(
                "list of objects",
                ObjectList.serializer(),
                ObjectList(
                    listOf(
                        SimpleObj("foo"),
                        SimpleObj("bar")
                    )
                ),
                """{
                |  objects: [
                |    {
                |      string: foo
                |    },
                |    {
                |      string: bar
                |    }
                |  ]
                |}
                """.trimMargin()
            ),

            arguments(
                "list of object lists",
                NestedList.serializer(),
                NestedList(listOf(listOf(Int.MIN_VALUE), listOf(Int.MAX_VALUE))),
                """{
                |  ints: [
                |    [
                |      ${Int.MIN_VALUE}
                |    ],
                |    [
                |      ${Int.MAX_VALUE}
                |    ]
                |  ]
                |}
                """.trimMargin()
            ),

            arguments(
                "map of object list",
                ObjectListMap.serializer(),
                ObjectListMap(
                    mapOf(
                        "123" to ObjectList(
                            listOf(SimpleObj("foo"))
                        )
                    )
                ),
                """{
                |  map: {
                |    123: {
                |      objects: [
                |        {
                |          string: foo
                |        }
                |      ]
                |    }
                |  }
                |}
                """.trimMargin()
            ),

            arguments(
                "periods",
                Periods.serializer(),
                Periods(
                    Period.ofDays(1), Period.ofWeeks(1), Period.ofMonths(1), Period.ofYears(1)
                ),
                """{
                |  period1: 1d,
                |  period2: 1w,
                |  period3: 1m,
                |  period4: 1y
                |}
                """.trimMargin()
            ),

            arguments(
                "durations",
                Durations.serializer(),
                Durations(
                    Duration.ofNanos(1), Duration.ofNanos(1000), Duration.ofMillis(1),
                    Duration.ofSeconds(1), Duration.ofMinutes(1), Duration.ofHours(1),
                    Duration.ofDays(1)
                ),
                """{
                |  duration1: 1ns,
                |  duration2: 1us,
                |  duration3: 1ms,
                |  duration4: 1s,
                |  duration5: 1m,
                |  duration6: 1h,
                |  duration7: 1d
                |}
                """.trimMargin()
            ),

            arguments(
                "memory sizes",
                MemSizes.serializer(),
                MemSizes(
                    memSize(0), memSize(1), memSize(2), memSize(3),
                    memSize(4), memSize(5), memSize(6)
                ),
                """{
                |  mem1: 1B,
                |  mem2: 1KiB,
                |  mem3: 1MiB,
                |  mem4: 1GiB,
                |  mem5: 1TiB,
                |  mem6: 1PiB,
                |  mem7: 1EiB
                |}
                """.trimMargin()
            )
        )

        private fun memSize(nPow: Int) =
            ConfigMemorySize.ofBytes(1024.0.pow(nPow.toDouble()).toLong())

        @DisplayName("Deserializer")
        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("dataProvider")
        fun <T> deserializer(
            name: String, serializer: KSerializer<T>, data: T, configText: String
        ) {
            // given
            val config = ConfigFactory.parseString(configText)

            // when
            val result = serializer.load(config)

            // then
            result shouldBe data
        }

        @DisplayName("Serializer")
        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("dataProvider")
        fun <T> serializer(
            name: String, serializer: KSerializer<T>, data: T, configText: String
        ) {
            // when
            val result = ConfigSerializer.stringify(serializer, data)

            // then
            result shouldBe configText
        }
    }

    @Nested
    inner class MissingKeyDeserializationTest {

        @Test
        fun `throw MissingFieldException on loading missing key`() {
            // given
            val config = ConfigFactory.parseString("{}")

            // when / then
            shouldThrow<MissingFieldException> {
                SimpleObj.serializer().load(config)
            }
        }

        @Test
        fun `throw MissingFieldException on loading missing key even for a nullable field`() {
            // given
            @Serializable
            data class OptionalValue(val optValue: String?)

            val config = ConfigFactory.parseString("{}")

            // when / then
            shouldThrow<MissingFieldException> {
                OptionalValue.serializer().load(config)
            }
        }

        @Test
        fun `if any, default value is used for missing key`() {
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
            val config = ConfigFactory.parseString("""{"string": "foo", "unknown": 123}""")

            // when
            val result = SimpleObj.serializer().load(config)

            // then
            result shouldBe SimpleObj("foo")
        }
    }


    @Suppress("BooleanLiteralArgument")
    @Test
    fun `StringBooleanSerializer works as expected`() {
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
