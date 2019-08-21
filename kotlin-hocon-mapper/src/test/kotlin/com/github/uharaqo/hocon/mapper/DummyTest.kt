package com.github.uharaqo.hocon.mapper

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test

class DummyTest {

    @Test
    fun test() {
        Dummy().hello() shouldBe "hello"
    }
}
