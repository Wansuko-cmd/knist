@file:Suppress("NonAsciiCharacters")

package com.wsr.initializer

import com.wsr.core.get
import kotlin.test.Test
import kotlin.test.assertTrue

class RandomTest {
    @Test
    fun `Randomのd1=デフォルトで_1_0から1_0の範囲で初期化する`() {
        val initializer = Random(seed = 42)

        val result = initializer.d1(
            input = listOf(10),
            output = listOf(10),
            size = 20,
        )

        // デフォルトの範囲は -1.0f ~ 1.0f
        for (i in 0 until 20) {
            assertTrue(result[i] >= -1.0f, "値が下限を下回っている: ${result[i]}")
            assertTrue(result[i] < 1.0f, "値が上限を超えている: ${result[i]}")
        }
    }

    @Test
    fun `Randomのd2=デフォルトで_1_0から1_0の範囲で初期化する`() {
        val initializer = Random(seed = 42)

        val result = initializer.d2(
            input = listOf(100),
            output = listOf(50),
            x = 10,
            y = 10,
        )

        // デフォルトの範囲は -1.0f ~ 1.0f
        for (i in 0 until 10) {
            for (j in 0 until 10) {
                assertTrue(result[i, j] >= -1.0f, "値が下限を下回っている: ${result[i, j]}")
                assertTrue(result[i, j] < 1.0f, "値が上限を超えている: ${result[i, j]}")
            }
        }
    }

    @Test
    fun `Randomのd3=デフォルトで_1_0から1_0の範囲で初期化する`() {
        val initializer = Random(seed = 123)

        val result = initializer.d3(
            input = listOf(3, 5, 5),
            output = listOf(64, 5, 5),
            x = 5,
            y = 5,
            z = 5,
        )

        // デフォルトの範囲は -1.0f ~ 1.0f
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                for (k in 0 until 5) {
                    assertTrue(result[i, j, k] >= -1.0f, "値が下限を下回っている: ${result[i, j, k]}")
                    assertTrue(result[i, j, k] < 1.0f, "値が上限を超えている: ${result[i, j, k]}")
                }
            }
        }
    }

    @Test
    fun `Randomのd4=4次元テンソルを指定範囲で初期化する`() {
        val initializer = Random(seed = 777, from = 0.0f, until = 2.0f)

        val result = initializer.d4(
            input = listOf(10),
            output = listOf(20),
            i = 2,
            j = 2,
            k = 2,
            l = 2,
        )

        // 指定した範囲内にあることを確認
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                for (k in 0 until 2) {
                    for (l in 0 until 2) {
                        assertTrue(result[i, j, k, l] >= 0.0f)
                        assertTrue(result[i, j, k, l] < 2.0f)
                    }
                }
            }
        }
    }
}
