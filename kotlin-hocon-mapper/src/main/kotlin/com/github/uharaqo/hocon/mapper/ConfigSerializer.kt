package com.github.uharaqo.hocon.mapper

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

interface ConfigSerializer {

    fun <T, S : SerializationStrategy<T>> stringify(serializer: S, obj: T): String

    companion object {

        @OptIn(ExperimentalSerializationApi::class)
        private val DEFAULT_SERIALIZER = object : ConfigSerializer {

            private val json = Json {
                encodeDefaults = true
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
                prettyPrintIndent = "  "
                useArrayPolymorphism = true
            }

            override fun <T, S : SerializationStrategy<T>> stringify(serializer: S, obj: T) =
                json.encodeToString(serializer, obj)
        }

        fun <T> stringify(serializer: KSerializer<T>, data: T): String =
            DEFAULT_SERIALIZER.stringify(serializer, data)
    }
}

inline fun <T> KSerializer<T>.stringify(data: T) = ConfigSerializer.stringify(this, data)
