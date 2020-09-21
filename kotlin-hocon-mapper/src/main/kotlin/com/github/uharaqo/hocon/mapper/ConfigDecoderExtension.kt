package com.github.uharaqo.hocon.mapper

import com.typesafe.config.Config
import kotlinx.serialization.DeserializationStrategy

inline fun <T, S : DeserializationStrategy<T>> S.load(config: Config): T =
    ConfigDecoder(config).decodeSerializableValue(this)
