@file:Suppress("NonAsciiCharacters")

package com.wsr.initializer

import com.wsr.core.get
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertTrue

class HeTest {
    @Test
    fun `Heのd1=fan_inに基づいて適切な範囲で初期化する`() {
        val initializer = He(seed = 42)

        // fan_in = 512
        val result = initializer.d1(
            input = listOf(512),
            output = listOf(256),
            size = 20,
        )

        // limit = sqrt(6 / 512) ≈ 0.1083f
        val expectedLimit = sqrt(6.0f / 512)

        // 全ての要素が範囲内にあることを確認
        for (i in 0 until 20) {
            assertTrue(result[i] >= -expectedLimit, "値が下限を下回っている: ${result[i]}")
            assertTrue(result[i] <= expectedLimit, "値が上限を超えている: ${result[i]}")
        }
    }

    @Test
    fun `Heのd2=fan_inに基づいて適切な範囲で初期化する`() {
        val initializer = He(seed = 42)

        // fan_in = 100
        val result = initializer.d2(
            input = listOf(100),
            output = listOf(50),
            x = 10,
            y = 10,
        )

        // limit = sqrt(6 / 100) ≈ 0.2449f
        val expectedLimit = sqrt(6.0f / 100)

        // 全ての要素が範囲内にあることを確認
        for (i in 0 until 10) {
            for (j in 0 until 10) {
                assertTrue(result[i, j] >= -expectedLimit, "値が下限を下回っている: ${result[i, j]}")
                assertTrue(result[i, j] <= expectedLimit, "値が上限を超えている: ${result[i, j]}")
            }
        }
    }

    @Test
    fun `Heのd3=畳み込み層の初期化に適切な範囲を使用する`() {
        val initializer = He(seed = 123)

        // fan_in = 3 * 5 * 5 = 75 (入力チャンネル × カーネルサイズ)
        val result = initializer.d3(
            input = listOf(3, 5, 5),
            output = listOf(64, 5, 5),
            x = 64,
            y = 3,
            z = 5,
        )

        // limit = sqrt(6 / 75) ≈ 0.2828f
        val expectedLimit = sqrt(6.0f / 75)

        // いくつかの要素を確認
        assertTrue(result[0, 0, 0] >= -expectedLimit)
        assertTrue(result[0, 0, 0] <= expectedLimit)
        assertTrue(result[32, 1, 2] >= -expectedLimit)
        assertTrue(result[32, 1, 2] <= expectedLimit)
    }

    @Test
    fun `Heのd4=4次元テンソルを適切に初期化する`() {
        val initializer = He(seed = 777)

        // fan_in = 10
        val result = initializer.d4(
            input = listOf(10),
            output = listOf(20),
            i = 2,
            j = 2,
            k = 2,
            l = 2,
        )

        // limit = sqrt(6 / 10) ≈ 0.7746f
        val expectedLimit = sqrt(6.0f / 10)

        // いくつかの要素を確認
        assertTrue(result[0, 0, 0, 0] >= -expectedLimit)
        assertTrue(result[0, 0, 0, 0] <= expectedLimit)
        assertTrue(result[1, 1, 1, 1] >= -expectedLimit)
        assertTrue(result[1, 1, 1, 1] <= expectedLimit)
    }
}
