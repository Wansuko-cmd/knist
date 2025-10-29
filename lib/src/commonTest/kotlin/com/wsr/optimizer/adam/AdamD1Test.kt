@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class AdamD1Test {
    @Test
    fun `AdamD1の_adapt=初回呼び出し時の動作`() {
        val adamD1 = AdamD1(rate = 0.001, momentum = 0.9, rms = 0.999, maxNorm = Double.MAX_VALUE, shape = listOf(2))

        // weight = [10.0, 20.0]
        val weight = IOType.d1(listOf(10.0, 20.0))
        // dw = [1, 2]
        val dw = IOType.d1(listOf(1.0, 2.0))

        // 初回: t=1
        // m_1 = 0.9 * 0 + 0.1 * [1, 2] = [0.1, 0.2]
        // v_1 = 0.999 * 0 + 0.001 * [1, 4] = [0.001, 0.004]
        // m̂_1 = [0.1, 0.2] / (1 - 0.9^1) = [0.1, 0.2] / 0.1 = [1, 2]
        // v̂_1 = [0.001, 0.004] / (1 - 0.999^1) = [0.001, 0.004] / 0.001 = [1, 4]
        // adapt = weight - 0.001 * [1, 2] / (sqrt([1, 4]) + 1e-8)
        val result = adamD1.adapt(weight, dw)

        // Adamは適応的に学習率を調整し、バイアス補正により初期ステップでも安定
        assertEquals(expected = 9.999, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 19.999, actual = result[1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `AdamD1の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamD1 = AdamD1(rate = 0.001, momentum = 0.9, rms = 0.999, maxNorm = Double.MAX_VALUE, shape = listOf(2))

        // 1回目
        var weight = IOType.d1(listOf(10.0, 10.0))
        val dw1 = IOType.d1(listOf(1.0, 1.0))
        val result1 = adamD1.adapt(weight, dw1)

        assertEquals(expected = 9.999, actual = result1[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.999, actual = result1[1], absoluteTolerance = 1e-10)

        // 2回目
        weight = result1
        val dw2 = IOType.d1(listOf(1.0, 1.0))
        val result2 = adamD1.adapt(weight, dw2)

        // モーメントが蓄積され、バイアス補正も変化する
        assertEquals(expected = 9.998, actual = result2[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.998, actual = result2[1], absoluteTolerance = 1e-10)
    }
}
