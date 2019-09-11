@file:UseSerializers(
    PeriodSerializer::class,
    DurationSerializer::class,
    ConfigMemorySizeSerializer::class
)

package com.github.uharaqo.hocon.mapper

import com.github.uharaqo.hocon.mapper.AdditionalModels.Durations
import com.github.uharaqo.hocon.mapper.AdditionalModels.MemSizes
import com.github.uharaqo.hocon.mapper.AdditionalModels.Periods
import com.github.uharaqo.hocon.mapper.BasicModels.BasicTypes
import com.github.uharaqo.hocon.mapper.BasicModels.SampleEnum
import com.github.uharaqo.hocon.mapper.BasicModels.SimpleObj
import com.github.uharaqo.hocon.mapper.CollectionModels.BasicTypeLists
import com.github.uharaqo.hocon.mapper.CollectionModels.NestedList
import com.github.uharaqo.hocon.mapper.CollectionModels.ObjectList
import com.github.uharaqo.hocon.mapper.CollectionModels.ObjectListMap
import com.github.uharaqo.hocon.mapper.NestedModels.Inner
import com.github.uharaqo.hocon.mapper.NestedModels.Outer
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigMemorySize
import io.kotlintest.shouldBe
import kotlinx.serialization.KSerializer
import kotlinx.serialization.UseSerializers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Duration
import java.time.Period
import java.util.stream.Stream
import kotlin.math.pow

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SerializerDeserializerTest {

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
        name: String,
        serializer: KSerializer<T>,
        data: T,
        configText: String
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
        name: String,
        serializer: KSerializer<T>,
        data: T,
        configText: String
    ) {
        // when
        val result = serializer.stringify(data)

        // then
        result shouldBe configText
    }
}
