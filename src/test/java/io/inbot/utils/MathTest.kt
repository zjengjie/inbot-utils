package io.inbot.utils

import org.assertj.core.api.Assertions
import org.testng.annotations.Test

import org.testng.Assert.*

@Test
class MathTest {

    fun testNormalize() {
        val normalized = Math.normalize(1.0, 1.0)
        Assertions.assertThat(normalized).isGreaterThan(0.4)
    }
}