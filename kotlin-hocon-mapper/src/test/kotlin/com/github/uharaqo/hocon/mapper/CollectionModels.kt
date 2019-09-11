package com.github.uharaqo.hocon.mapper

import kotlinx.serialization.Serializable

interface CollectionModels {

    @Serializable
    data class BasicTypeLists(
        val char: List<Char>,
        val string: List<String>,
        val bool: List<Boolean>,
        val byte: List<Byte>,
        val int: List<Int>,
        val long: List<Long>,
        val short: List<Short>,
        val float: List<Float>,
        val double: List<Double>,
        val enum: List<BasicModels.SampleEnum>
    )

    @Serializable
    data class NestedList(val ints: List<List<Int>>)

    @Serializable
    data class ObjectList(val objects: List<BasicModels.SimpleObj>)

    @Serializable
    data class ObjectListMap(
        val map: Map<String, ObjectList>
    )
}