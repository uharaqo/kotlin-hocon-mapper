@file:UseSerializers(
    PeriodSerializer::class,
    DurationSerializer::class,
    ConfigMemorySizeSerializer::class
)

package com.github.uharaqo.hocon.mapper

import com.typesafe.config.ConfigMemorySize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.Duration
import java.time.Period

interface AdditionalModels {

    @Serializable
    data class Periods(
        val period1: Period,
        val period2: Period,
        val period3: Period,
        val period4: Period
    )

    @Serializable
    data class Durations(
        val duration1: Duration,
        val duration2: Duration,
        val duration3: Duration,
        val duration4: Duration,
        val duration5: Duration,
        val duration6: Duration,
        val duration7: Duration
    )

    @Serializable
    data class MemSizes(
        val mem1: ConfigMemorySize,
        val mem2: ConfigMemorySize,
        val mem3: ConfigMemorySize,
        val mem4: ConfigMemorySize,
        val mem5: ConfigMemorySize,
        val mem6: ConfigMemorySize,
        val mem7: ConfigMemorySize
    )
}