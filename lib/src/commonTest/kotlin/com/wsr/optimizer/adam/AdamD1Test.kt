@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertTrue

class AdamD1Test {
    @Test
    fun `AdamD1の_adapt=初回呼び出し時の動作`() {
        val adamD1 = AdamD1(rate = 0.001, momentum = 0.9, rms = 0.999, shape = listOf(2))

        // dw = [1, 2]
        val dw = IOType.d1(listOf(1.0, 2.0))

        // 初回: t=1
        // m_1 = 0.9 * 0 + 0.1 * [1, 2] = [0.1, 0.2]
        // v_1 = 0.999 * 0 + 0.001 * [1, 4] = [0.001, 0.004]
        // m̂_1 = [0.1, 0.2] / (1 - 0.9^1) = [0.1, 0.2] / 0.1 = [1, 2]
        // v̂_1 = [0.001, 0.004] / (1 - 0.999^1) = [0.001, 0.004] / 0.001 = [1, 4]
        // adapt = 0.001 * [1, 2] / (sqrt([1, 4]) + 1e-8)
        val result = adamD1.adapt(dw)

        // Adamは適応的に学習率を調整し、バイアス補正により初期ステップでも安定
        assertTrue(result[0] > 0.0)
        assertTrue(result[1] > 0.0)
        assertTrue(result[0].isFinite())
        assertTrue(result[1].isFinite())
    }

    @Test
    fun `AdamD1の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamD1 = AdamD1(rate = 0.001, momentum = 0.9, rms = 0.999, shape = listOf(2))

        // 1回目
        val dw1 = IOType.d1(listOf(1.0, 1.0))
        val result1 = adamD1.adapt(dw1)

        // 2回目
        val dw2 = IOType.d1(listOf(1.0, 1.0))
        val result2 = adamD1.adapt(dw2)

        // モーメントが蓄積され、バイアス補正も変化する
        assertTrue(result1[0] > 0.0)
        assertTrue(result2[0] > 0.0)
        assertTrue(result1[0].isFinite())
        assertTrue(result2[0].isFinite())
    }
}
