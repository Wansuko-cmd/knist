@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class AdamWD1Test {
    @Test
    fun `AdamWD1の_adapt=初回呼び出し時の動作`() {
        val adamWD1 = AdamWD1(rate = 0.001, momentum = 0.9, rms = 0.999, decay = 0.01, shape = listOf(2))

        // weight = [10.0, 20.0]
        val weight = IOType.d1(listOf(10.0, 20.0))
        // dw = [1, 2]
        val dw = IOType.d1(listOf(1.0, 2.0))

        // 初回: t=1
        // m_1 = 0.9 * 0 + 0.1 * [1, 2] = [0.1, 0.2]
        // v_1 = 0.999 * 0 + 0.001 * [1, 4] = [0.001, 0.004]
        // m̂_1 = [0.1, 0.2] / (1 - 0.9^1) = [0.1, 0.2] / 0.1 = [1, 2]
        // v̂_1 = [0.001, 0.004] / (1 - 0.999^1) = [0.001, 0.004] / 0.001 = [1, 4]
        // adapt = (1 - 0.01) * weight - 0.001 * [1, 2] / (sqrt([1, 4]) + 1e-8)
        //       = 0.99 * [10, 20] - 0.001 * [1, 2] / [1, 2]
        //       = [9.9, 19.8] - [0.001, 0.001]
        //       = [9.899, 19.799]
        val result = adamWD1.adapt(weight, dw)

        // AdamWはWeight Decayにより、通常のAdamよりもさらに重みが減少する
        assertEquals(expected = 9.899, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 19.799, actual = result[1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `AdamWD1の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamWD1 = AdamWD1(rate = 0.001, momentum = 0.9, rms = 0.999, decay = 0.01, shape = listOf(2))

        // 1回目
        var weight = IOType.d1(listOf(10.0, 10.0))
        val dw1 = IOType.d1(listOf(1.0, 1.0))
        val result1 = adamWD1.adapt(weight, dw1)

        assertEquals(expected = 9.899, actual = result1[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.899, actual = result1[1], absoluteTolerance = 1e-10)

        // 2回目
        weight = result1
        val dw2 = IOType.d1(listOf(1.0, 1.0))
        val result2 = adamWD1.adapt(weight, dw2)

        // モーメントが蓄積され、バイアス補正も変化し、Weight Decayも適用される
        // result1から減少していることを確認
        assertEquals(expected = 9.799, actual = result2[0], absoluteTolerance = 1e-4)
        assertEquals(expected = 9.799, actual = result2[1], absoluteTolerance = 1e-4)
    }
}
