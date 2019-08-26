package com.github.uharaqo.hocon.mapper

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigMemorySize
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.internal.SerialClassDescImpl
import java.time.Duration
import java.time.Period

class PeriodSerializer : CustomConfigSerializer<Period>("java.time.Period") {

    override fun deserializeConfig(config: Config, key: String): Period =
        config.getPeriod(key)
}

class DurationSerializer : CustomConfigSerializer<Duration>("java.time.Duration") {

    override fun deserializeConfig(config: Config, key: String): Duration =
        config.getDuration(key)
}

class ConfigMemorySizeSerializer
    : CustomConfigSerializer<ConfigMemorySize>("com.typesafe.config.ConfigMemorySize") {

    override fun deserializeConfig(config: Config, key: String): ConfigMemorySize =
        config.getMemorySize(key)
}

class StringBooleanSerializer : CustomConfigSerializer<Boolean>("kotlin.Boolean") {

    override fun deserializeConfig(config: Config, key: String): Boolean =
        config.getBoolean(key)
}

abstract class CustomConfigSerializer<T>(name: String) : KSerializer<T> {

    override val descriptor = SerialClassDescImpl(name)

    override fun deserialize(decoder: Decoder): T {
        val value = decoder.decodeString()
        val config = ConfigFactory.parseString("$DUMMY_KEY: $value")
        return deserializeConfig(config, DUMMY_KEY)
    }

    abstract fun deserializeConfig(config: Config, key: String): T

    override fun serialize(encoder: Encoder, obj: T) {
        // TODO
    }

    companion object {
        private const val DUMMY_KEY = "k"
    }
}