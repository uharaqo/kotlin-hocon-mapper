@file:UseSerializers(
    PeriodSerializer::class,
    DurationSerializer::class,
    ConfigMemorySizeSerializer::class,
    StringBooleanSerializer::class
)

package com.github.uharaqo.hocon.mapper

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigMemorySize
import io.kotlintest.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Period
import kotlin.math.pow

class DeserializerAdditionalTypesTest {

    @Test
    fun `PeriodSerializer works as expected`() {
        // given

        @Serializable
        data class Data(
            val period1: Period, val period2: Period, val period3: Period, val period4: Period
        )

        val config = ConfigFactory.parseString(
            """
            |{
            |  period1: 1d
            |  period2: 1w
            |  period3: 1m
            |  period4: 1y
            |}
            """.trimMargin()
        )

        // when
        val result = Data.serializer().load(config)

        // then
        result shouldBe Data(
            Period.ofDays(1),
            Period.ofWeeks(1),
            Period.ofMonths(1),
            Period.ofYears(1)
        )
    }

    @Test
    fun `DurationSerializer works as expected`() {
        // given

        @Serializable
        data class Data(
            val duration1: Duration, val duration2: Duration, val duration3: Duration,
            val duration4: Duration, val duration5: Duration, val duration6: Duration,
            val duration7: Duration
        )

        val config = ConfigFactory.parseString(
            """
            |{
            |  duration1: 1ns
            |  duration2: 1us
            |  duration3: 1ms
            |  duration4: 1s
            |  duration5: 1m
            |  duration6: 1h
            |  duration7: 1d
            |}
            """.trimMargin()
        )

        // when
        val result = Data.serializer().load(config)

        // then
        result shouldBe Data(
            Duration.ofNanos(1),
            Duration.ofNanos(1000),
            Duration.ofMillis(1),
            Duration.ofSeconds(1),
            Duration.ofMinutes(1),
            Duration.ofHours(1),
            Duration.ofDays(1)
        )
    }

    @Test
    fun `ConfigMemorySizeSerializer works as expected`() {
        // given

        @Serializable
        data class Data(
            val mem1: ConfigMemorySize, val mem2: ConfigMemorySize, val mem3: ConfigMemorySize,
            val mem4: ConfigMemorySize, val mem5: ConfigMemorySize, val mem6: ConfigMemorySize,
            val mem7: ConfigMemorySize
        )

        val config = ConfigFactory.parseString(
            """
            |{
            |  mem1: 1B
            |  mem2: 1KiB
            |  mem3: 1MiB
            |  mem4: 1GiB
            |  mem5: 1TiB
            |  mem6: 1PiB
            |  mem7: 1EiB
            |}
            """.trimMargin()
        )

        // when
        val result = Data.serializer().load(config)

        // then
        result shouldBe Data(
            ConfigMemorySize.ofBytes(1024.0.pow(0.0).toLong()),
            ConfigMemorySize.ofBytes(1024.0.pow(1.0).toLong()),
            ConfigMemorySize.ofBytes(1024.0.pow(2.0).toLong()),
            ConfigMemorySize.ofBytes(1024.0.pow(3.0).toLong()),
            ConfigMemorySize.ofBytes(1024.0.pow(4.0).toLong()),
            ConfigMemorySize.ofBytes(1024.0.pow(5.0).toLong()),
            ConfigMemorySize.ofBytes(1024.0.pow(6.0).toLong())
        )
    }

    @Suppress("BooleanLiteralArgument")
    @Test
    fun `StringBooleanSerializer works as expected`() {
        // given

        @Serializable
        data class Data(
            val bool1: Boolean, val bool2: Boolean, val bool3: Boolean,
            val bool4: Boolean, val bool5: Boolean, val bool6: Boolean
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