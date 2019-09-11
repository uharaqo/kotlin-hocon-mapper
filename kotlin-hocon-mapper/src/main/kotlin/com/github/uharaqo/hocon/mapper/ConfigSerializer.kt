package com.github.uharaqo.hocon.mapper

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

interface ConfigSerializer {

    fun <T, S : SerializationStrategy<T>> stringify(serializer: S, obj: T): String

    companion object {

        private val DEFAULT_SERIALIZER = object : ConfigSerializer {

            @UseExperimental(UnstableDefault::class)
            private val json = Json(
                JsonConfiguration(
                    strictMode = false,
                    useArrayPolymorphism = true,
                    prettyPrint = true,
                    indent = "  ",
                    unquoted = true
                )
            )

            override fun <T, S : SerializationStrategy<T>> stringify(serializer: S, obj: T) =
                json.stringify(serializer, obj)
        }

        fun <T> stringify(serializer: KSerializer<T>, data: T): String =
            DEFAULT_SERIALIZER.stringify(serializer, data)
    }
}

inline fun <T> KSerializer<T>.stringify(data: T) = ConfigSerializer.stringify(this, data)
