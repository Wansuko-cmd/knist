@file:Suppress("NonAsciiCharacters")

package com.wsr.initializer

import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class XavierTest {
    @Test
    fun `Xavierのd1=fan_inとfan_outに基づいて適切な範囲で初期化する`() {
        val initializer = Xavier(seed = 42)

        // fan_in = 512, fan_out = 256
        val result = initializer.d1(
            input = listOf(512),
            output = listOf(256),
            size = 20,
        )

        // limit = sqrt(6 / (512 + 256)) = sqrt(6 / 768) ≈ 0.0884
        val expectedLimit = sqrt(6.0 / 768)

        // 全ての要素が範囲内にあることを確認
        for (i in 0 until 20) {
            assertTrue(result[i] >= -expectedLimit, "値が下限を下回っている: ${result[i]}")
            assertTrue(result[i] <= expectedLimit, "値が上限を超えている: ${result[i]}")
        }
    }

    @Test
    fun `Xavierのd2=fan_inとfan_outに基づいて適切な範囲で初期化する`() {
        val initializer = Xavier(seed = 42)

        // fan_in = 100, fan_out = 50
        val result = initializer.d2(
            input = listOf(100),
            output = listOf(50),
            x = 10,
            y = 10,
        )

        // limit = sqrt(6 / (100 + 50)) = sqrt(6 / 150) ≈ 0.2
        val expectedLimit = sqrt(6.0 / 150)

        // 全ての要素が範囲内にあることを確認
        for (i in 0 until 10) {
            for (j in 0 until 10) {
                assertTrue(result[i, j] >= -expectedLimit, "値が下限を下回っている: ${result[i, j]}")
                assertTrue(result[i, j] <= expectedLimit, "値が上限を超えている: ${result[i, j]}")
            }
        }
    }

    @Test
    fun `Xavierのd3=fan_inとfan_outの両方を考慮した初期化を行う`() {
        val initializer = Xavier(seed = 123)

        // fan_in = 3 * 5 * 5 = 75, fan_out = 64 * 5 * 5 = 1600
        val result = initializer.d3(
            input = listOf(3, 5, 5),
            output = listOf(64, 5, 5),
            x = 64,
            y = 3,
            z = 5,
        )

        // limit = sqrt(6 / (75 + 1600)) = sqrt(6 / 1675) ≈ 0.0599
        val expectedLimit = sqrt(6.0 / 1675)

        // いくつかの要素を確認
        assertTrue(result[0, 0, 0] >= -expectedLimit)
        assertTrue(result[0, 0, 0] <= expectedLimit)
        assertTrue(result[32, 1, 2] >= -expectedLimit)
        assertTrue(result[32, 1, 2] <= expectedLimit)
    }

    @Test
    fun `Xavierのd4=4次元テンソルを適切に初期化する`() {
        val initializer = Xavier(seed = 777)

        // fan_in = 10, fan_out = 20
        val result = initializer.d4(
            input = listOf(10),
            output = listOf(20),
            i = 2,
            j = 2,
            k = 2,
            n = 2,
        )

        // limit = sqrt(6 / (10 + 20)) = sqrt(6 / 30) ≈ 0.4472
        val expectedLimit = sqrt(6.0 / 30)

        // いくつかの要素を確認
        assertTrue(result[0, 0, 0, 0] >= -expectedLimit)
        assertTrue(result[0, 0, 0, 0] <= expectedLimit)
        assertTrue(result[1, 1, 1, 1] >= -expectedLimit)
        assertTrue(result[1, 1, 1, 1] <= expectedLimit)
    }
}
