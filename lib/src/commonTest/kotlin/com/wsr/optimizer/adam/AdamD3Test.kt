@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class AdamD3Test {
    @Test
    fun `AdamD3の_adapt=初回呼び出し時の動作`() {
        val adamD3 =
            AdamD3(rate = 0.001, momentum = 0.9, rms = 0.999, maxNorm = Float.MAX_VALUE, shape = listOf(1, 1, 2))

        // weight = [[[10, 20]]]
        val weight = IOType.d3(1, 1, 2) { _, _, z -> (z + 1) * 10.0 }
        // dw = [[[1, 2]]]
        val dw = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() }

        val result = adamD3.adapt(weight, dw)

        // Adamは適応的に学習率を調整し、バイアス補正により初期ステップでも安定
        assertEquals(expected = 9.999, actual = result[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 19.999, actual = result[0, 0, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `AdamD3の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamD3 =
            AdamD3(rate = 0.001, momentum = 0.9, rms = 0.999, maxNorm = Float.MAX_VALUE, shape = listOf(1, 1, 2))

        // 1回目
        var weight = IOType.d3(1, 1, 2) { _, _, _ -> 10.0 }
        val dw1 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() }
        val result1 = adamD3.adapt(weight, dw1)

        assertEquals(expected = 9.999, actual = result1[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.999, actual = result1[0, 0, 1], absoluteTolerance = 1e-10)

        // 2回目
        weight = result1
        val dw2 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() }
        val result2 = adamD3.adapt(weight, dw2)

        assertEquals(expected = 9.998, actual = result2[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.998, actual = result2[0, 0, 1], absoluteTolerance = 1e-10)
    }
}
