@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class AdamWD3Test {
    @Test
    fun `AdamWD3の_adapt=初回呼び出し時の動作`() {
        val adamWD3 =
            AdamWD3(
                rate = 0.001,
                momentum = 0.9,
                rms = 0.999,
                decay = 0.01,
                maxNorm = Double.MAX_VALUE,
                shape = listOf(1, 1, 2),
            )

        // weight = [[[10, 20]]]
        val weight = IOType.d3(1, 1, 2) { _, _, z -> (z + 1) * 10.0 }
        // dw = [[[1, 2]]]
        val dw = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }

        val result = adamWD3.adapt(weight, dw)

        // AdamWはWeight Decayにより、通常のAdamよりもさらに重みが減少する
        // (1 - 0.001*0.01) * [10, 20] - 0.001 * [1, 2] / sqrt([1, 4])
        // = 0.99999 * [10, 20] - 0.001 * [1, 1]
        // = [9.9989, 19.9988]
        assertEquals(expected = 9.9989, actual = result[0, 0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 19.9988, actual = result[0, 0, 1], absoluteTolerance = 1e-4)
    }

    @Test
    fun `AdamWD3の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamWD3 =
            AdamWD3(
                rate = 0.001,
                momentum = 0.9,
                rms = 0.999,
                decay = 0.01,
                maxNorm = Double.MAX_VALUE,
                shape = listOf(1, 1, 2),
            )

        // 1回目
        var weight = IOType.d3(1, 1, 2) { _, _, _ -> 10.0 }
        val dw1 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }
        val result1 = adamWD3.adapt(weight, dw1)

        // dw1 = [[[1, 2]]]
        // [0,0,0]: dw=1: (1-0.00001)*10 - 0.001*1/sqrt(1) = 9.9989
        // [0,0,1]: dw=2: (1-0.00001)*10 - 0.001*2/sqrt(4) = 9.9989
        assertEquals(expected = 9.9989, actual = result1[0, 0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 9.9989, actual = result1[0, 0, 1], absoluteTolerance = 1e-4)

        // 2回目
        weight = result1
        val dw2 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }
        val result2 = adamWD3.adapt(weight, dw2)

        // モーメントが蓄積され、バイアス補正も変化し、Weight Decayも適用される
        // 各要素でさらに減少
        assertEquals(expected = 9.9978, actual = result2[0, 0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 9.9978, actual = result2[0, 0, 1], absoluteTolerance = 1e-4)
    }
}
