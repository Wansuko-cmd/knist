@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.network.initializer

import com.wsr.knist.core.unwrap
import com.wsr.knist.network.networkTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class FixedTest {
    @Test
    fun `d1=指定値で初期化`() = networkTestRule {
        val target = Fixed(value = 0.5f)

        val actual = target.d1(input = listOf(3), output = listOf(3), size = 3)

        for (value in actual.unwrap()) {
            assertEquals(expected = 0.5f, actual = value)
        }
    }

    @Test
    fun `d2=指定値で初期化`() = networkTestRule {
        val target = Fixed(value = 0.5f)

        val actual = target.d2(input = listOf(2, 2), output = listOf(2, 2), i = 2, j = 2)

        for (value in actual.unwrap()) {
            assertEquals(expected = 0.5f, actual = value)
        }
    }

    @Test
    fun `d3=指定値で初期化`() = networkTestRule {
        val target = Fixed(value = 0.5f)

        val actual = target.d3(input = listOf(2, 2, 2), output = listOf(2, 2, 2), i = 2, j = 2, k = 2)

        for (value in actual.unwrap()) {
            assertEquals(expected = 0.5f, actual = value)
        }
    }

    @Test
    fun `d4=指定値で初期化`() = networkTestRule {
        val target = Fixed(value = 0.5f)

        val actual = target.d4(input = listOf(2, 2, 2, 2), output = listOf(2, 2, 2, 2), i = 2, j = 2, k = 2, l = 2)

        for (value in actual.unwrap()) {
            assertEquals(expected = 0.5f, actual = value)
        }
    }
}
