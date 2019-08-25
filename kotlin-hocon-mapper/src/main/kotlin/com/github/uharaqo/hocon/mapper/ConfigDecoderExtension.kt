package com.github.uharaqo.hocon.mapper

import com.typesafe.config.Config
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.decode

inline fun <reified T, S : DeserializationStrategy<T>> S.load(config: Config): T =
    ConfigDecoder(config).decode(this)
