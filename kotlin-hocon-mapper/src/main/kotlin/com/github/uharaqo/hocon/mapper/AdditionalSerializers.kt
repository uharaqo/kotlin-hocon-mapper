package com.github.uharaqo.hocon.mapper

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigMemorySize
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.internal.SerialClassDescImpl
import java.time.Duration
import java.time.Period

class PeriodSerializer : CustomConfigSerializer<Period>(DESCRIPTOR) {

    override fun deserializeConfig(config: Config, key: String): Period =
        config.getPeriod(key)

    override fun serialize(encoder: Encoder, obj: Period) = encoder.encodeString(obj.convert())

    private fun Period.convert(): String = when {
        days > 0 -> {
            when {
                months > 0 || years > 0 -> throw SerializationException(getErrorMessage())
                days % 7 == 0 -> "${days / 7}w"
                else -> "${days}d"
            }
        }
        months > 0 -> {
            when {
                years > 0 -> throw SerializationException(getErrorMessage())
                else -> "${months}m"
            }
        }
        years > 0 -> "${years}y"
        else -> "0d"
    }

    private fun Period.getErrorMessage() =
        "Period ${this.years}y ${this.months}m ${this.days}d is not convertible"

    companion object {
        private val DESCRIPTOR = SerialClassDescImpl("java.time.Period")
    }
}

class DurationSerializer : CustomConfigSerializer<Duration>(DESCRIPTOR) {

    override fun deserializeConfig(config: Config, key: String): Duration =
        config.getDuration(key)

    override fun serialize(encoder: Encoder, obj: Duration) = encoder.encodeString(obj.convert())

    private fun Duration.convert(): String {
        return when {
            nano > 0 -> {
                if (seconds > 0L) {
                    "${toNanos()}ns"
                } else {
                    when {
                        nano % 1000_000 == 0 -> "${toMillis()}ms"
                        nano % 1000 == 0 -> "${nano / 1000}us"
                        else -> "${toNanos()}ns"
                    }
                }
            }
            seconds > 0 -> {
                when {
                    seconds % 86400 == 0L -> "${toDays()}d"
                    seconds % 3600 == 0L -> "${toHours()}h"
                    seconds % 60 == 0L -> "${toMinutes()}m"
                    else -> "${seconds}s"
                }
            }
            else -> "0s"
        }
    }

    companion object {
        private val DESCRIPTOR = SerialClassDescImpl("java.time.Duration")
    }
}

class ConfigMemorySizeSerializer : CustomConfigSerializer<ConfigMemorySize>(DESCRIPTOR) {

    override fun deserializeConfig(config: Config, key: String): ConfigMemorySize =
        config.getMemorySize(key)

    override fun serialize(encoder: Encoder, obj: ConfigMemorySize) =
        encoder.encodeString(obj.convert())

    private fun ConfigMemorySize.convert(): String {
        var i = 0
        var value = this.toBytes()
        while (value % 1024 == 0L && i < MEMORY_UNITS.size - 1) {
            i++
            value /= 1024
        }
        return "$value${MEMORY_UNITS[i]}"
    }

    companion object {

        private val MEMORY_UNITS = arrayOf("B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB")
        private val DESCRIPTOR = SerialClassDescImpl("com.typesafe.config.ConfigMemorySize")
    }
}

class StringBooleanSerializer : CustomConfigSerializer<Boolean>(DESCRIPTOR) {

    override fun deserializeConfig(config: Config, key: String): Boolean =
        config.getBoolean(key)

    override fun serialize(encoder: Encoder, obj: Boolean) =
        encoder.encodeBoolean(obj)

    companion object {
        private val DESCRIPTOR = SerialClassDescImpl("kotlin.Boolean")
    }
}

abstract class CustomConfigSerializer<T>
    (override val descriptor: SerialDescriptor) : KSerializer<T> {

    override fun deserialize(decoder: Decoder): T {
        val value = decoder.decodeString()
        val config = ConfigFactory.parseString("$DUMMY_KEY: $value")
        return deserializeConfig(config, DUMMY_KEY)
    }

    abstract fun deserializeConfig(config: Config, key: String): T

    companion object {
        private const val DUMMY_KEY = "k"
    }
}
