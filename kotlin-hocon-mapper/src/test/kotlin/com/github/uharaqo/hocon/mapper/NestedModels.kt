package com.github.uharaqo.hocon.mapper

import kotlinx.serialization.Serializable

interface NestedModels {
    @Serializable
    data class Inner(val obj: BasicModels.SimpleObj)

    @Serializable
    data class Outer(val inner: Inner)
}