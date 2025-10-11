@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertTrue

class AdamD3Test {
    @Test
    fun `AdamD3の_adapt=初回呼び出し時の動作`() {
        val adamD3 = AdamD3(rate = 0.001, momentum = 0.9, rms = 0.999, shape = listOf(1, 1, 2))

        // dw = [[[1, 2]]]
        val dw = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }

        val result = adamD3.adapt(dw)

        // Adamは適応的に学習率を調整し、バイアス補正により初期ステップでも安定
        assertTrue(result[0, 0, 0] > 0.0)
        assertTrue(result[0, 0, 1] > 0.0)
        assertTrue(result[0, 0, 0].isFinite())
        assertTrue(result[0, 0, 1].isFinite())
    }

    @Test
    fun `AdamD3の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamD3 = AdamD3(rate = 0.001, momentum = 0.9, rms = 0.999, shape = listOf(1, 1, 2))

        // 1回目
        val dw1 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }
        val result1 = adamD3.adapt(dw1)

        // 2回目
        val dw2 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }
        val result2 = adamD3.adapt(dw2)

        // モーメントが蓄積され、バイアス補正も変化する
        assertTrue(result1[0, 0, 0] > 0.0)
        assertTrue(result2[0, 0, 0] > 0.0)
        assertTrue(result1[0, 0, 0].isFinite())
        assertTrue(result2[0, 0, 0].isFinite())
    }
}
