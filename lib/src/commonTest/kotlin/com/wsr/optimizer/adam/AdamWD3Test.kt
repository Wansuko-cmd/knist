@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class AdamWD3Test {
    @Test
    fun `AdamWD3の_adapt=初回呼び出し時の動作`() {
        val adamWD3 = AdamWD3(rate = 0.001, momentum = 0.9, rms = 0.999, decay = 0.01, shape = listOf(1, 1, 2))

        // weight = [[[10, 20]]]
        val weight = IOType.d3(1, 1, 2) { _, _, z -> (z + 1) * 10.0 }
        // dw = [[[1, 2]]]
        val dw = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }

        val result = adamWD3.adapt(weight, dw)

        // AdamWはWeight Decayにより、通常のAdamよりもさらに重みが減少する
        assertEquals(expected = 9.899, actual = result[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 19.799, actual = result[0, 0, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `AdamWD3の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamWD3 = AdamWD3(rate = 0.001, momentum = 0.9, rms = 0.999, decay = 0.01, shape = listOf(1, 1, 2))

        // 1回目
        var weight = IOType.d3(1, 1, 2) { _, _, _ -> 10.0 }
        val dw1 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }
        val result1 = adamWD3.adapt(weight, dw1)

        assertEquals(expected = 9.899, actual = result1[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.899, actual = result1[0, 0, 1], absoluteTolerance = 1e-10)

        // 2回目
        weight = result1
        val dw2 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }
        val result2 = adamWD3.adapt(weight, dw2)

        // モーメントが蓄積され、バイアス補正も変化し、Weight Decayも適用される
        // result1から減少していることを確認
        assertEquals(expected = 9.799, actual = result2[0, 0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 9.799, actual = result2[0, 0, 1], absoluteTolerance = 1e-4)
    }
}
