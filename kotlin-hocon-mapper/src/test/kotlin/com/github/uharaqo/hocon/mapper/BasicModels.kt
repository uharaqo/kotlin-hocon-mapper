package com.github.uharaqo.hocon.mapper

import kotlinx.serialization.Serializable

interface BasicModels {

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