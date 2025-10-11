@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertTrue

class AdamD2Test {
    @Test
    fun `AdamD2の_adapt=初回呼び出し時の動作`() {
        val adamD2 = AdamD2(rate = 0.001, momentum = 0.9, rms = 0.999)

        // dw = [[1, 2]]
        val dw = IOType.d2(1, 2) { _, y -> (y + 1).toDouble() }

        val result = adamD2.adapt(dw)

        // Adamは適応的に学習率を調整し、バイアス補正により初期ステップでも安定
        assertTrue(result[0, 0] > 0.0)
        assertTrue(result[0, 1] > 0.0)
        assertTrue(result[0, 0].isFinite())
        assertTrue(result[0, 1].isFinite())
    }

    @Test
    fun `AdamD2の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamD2 = AdamD2(rate = 0.001, momentum = 0.9, rms = 0.999)

        // 1回目
        val dw1 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val result1 = adamD2.adapt(dw1)

        // 2回目
        val dw2 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val result2 = adamD2.adapt(dw2)

        // モーメントが蓄積され、バイアス補正も変化する
        assertTrue(result1[0, 0] > 0.0)
        assertTrue(result2[0, 0] > 0.0)
        assertTrue(result1[0, 0].isFinite())
        assertTrue(result2[0, 0].isFinite())
    }
}
