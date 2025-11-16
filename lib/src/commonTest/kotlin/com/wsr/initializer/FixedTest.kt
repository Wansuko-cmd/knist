@file:Suppress("NonAsciiCharacters")

package com.wsr.initializer

import kotlin.test.Test
import kotlin.test.assertEquals

class FixedTest {
    @Test
    fun `Fixedのd1=指定した値で全要素を初期化する`() {
        val initializer = Fixed(value = 0.5f)

        val result = initializer.d1(
            input = listOf(10),
            output = listOf(10),
            size = 3,
        )

        assertEquals(expected = 0.5f, actual = result[0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.5f, actual = result[1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.5f, actual = result[2], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `Fixedのd2=指定した値で全要素を初期化する`() {
        val initializer = Fixed(value = 1.0f)

        val result = initializer.d2(
            input = listOf(5),
            output = listOf(3),
            x = 2,
            y = 3,
        )

        assertEquals(expected = 1.0f, actual = result[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 1.0f, actual = result[0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 1.0f, actual = result[0, 2], absoluteTolerance = 1e-6f)
        assertEquals(expected = 1.0f, actual = result[1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 1.0f, actual = result[1, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 1.0f, actual = result[1, 2], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `Fixedのd3=指定した値で全要素を初期化する`() {
        val initializer = Fixed(value = 0.0f)

        val result = initializer.d3(
            input = listOf(4, 3),
            output = listOf(2, 3),
            x = 2,
            y = 2,
            z = 2,
        )

        // 全ての要素が0.0であることを確認
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                for (k in 0 until 2) {
                    assertEquals(expected = 0.0f, actual = result[i, j, k], absoluteTolerance = 1e-6f)
                }
            }
        }
    }

    @Test
    fun `Fixedのd4=指定した値で全要素を初期化する`() {
        val initializer = Fixed(value = -1.5f)

        val result = initializer.d4(
            input = listOf(3, 3, 3),
            output = listOf(2, 3, 3),
            i = 2,
            j = 2,
            k = 2,
            n = 2,
        )

        // いくつかの要素を確認
        assertEquals(expected = -1.5f, actual = result[0, 0, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = -1.5f, actual = result[0, 1, 1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = -1.5f, actual = result[1, 0, 1, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = -1.5f, actual = result[1, 1, 1, 1], absoluteTolerance = 1e-6f)
    }
}
