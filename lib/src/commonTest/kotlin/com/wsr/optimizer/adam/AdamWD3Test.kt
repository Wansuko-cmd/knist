@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

import com.wsr.get

class AdamWD3Test {
    @Test
    fun `AdamWD3の_adapt=初回呼び出し時の動作`() {
        val adamWD3 =
            AdamWD3(
                rate = 0.001f,
                momentum = 0.9f,
                rms = 0.999f,
                decay = 0.01f,
                maxNorm = Float.MAX_VALUE,
                shape = listOf(1, 1, 2),
            )

        // weight = [[[10, 20]]]
        val weight = IOType.d3(1, 1, 2) { _, _, z -> (z + 1) * 10.0f }
        // dw = [[[1, 2]]]
        val dw = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() }

        val result = adamWD3.adapt(weight, dw)

        // AdamWはWeight Decayにより、通常のAdamよりもさらに重みが減少する
        // (1 - 0.001f*0.01f) * [10, 20] - 0.001f * [1, 2] / sqrt([1, 4])
        // = 0.99999f * [10, 20] - 0.001f * [1, 1]
        // = [9.9989f, 19.9988f]
        assertEquals(expected = 9.9989f, actual = result[0, 0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 19.9988f, actual = result[0, 0, 1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `AdamWD3の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamWD3 =
            AdamWD3(
                rate = 0.001f,
                momentum = 0.9f,
                rms = 0.999f,
                decay = 0.01f,
                maxNorm = Float.MAX_VALUE,
                shape = listOf(1, 1, 2),
            )

        // 1回目
        var weight = IOType.d3(1, 1, 2) { _, _, _ -> 10.0f }
        val dw1 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() }
        val result1 = adamWD3.adapt(weight, dw1)

        // dw1 = [[[1, 2]]]
        // [0,0,0]: dw=1: (1-0.00001f)*10 - 0.001f*1/sqrt(1) = 9.9989f
        // [0,0,1]: dw=2: (1-0.00001f)*10 - 0.001f*2/sqrt(4) = 9.9989f
        assertEquals(expected = 9.9989f, actual = result1[0, 0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 9.9989f, actual = result1[0, 0, 1], absoluteTolerance = 1e-4f)

        // 2回目
        weight = result1
        val dw2 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() }
        val result2 = adamWD3.adapt(weight, dw2)

        // モーメントが蓄積され、バイアス補正も変化し、Weight Decayも適用される
        // 各要素でさらに減少
        assertEquals(expected = 9.9978f, actual = result2[0, 0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 9.9978f, actual = result2[0, 0, 1], absoluteTolerance = 1e-4f)
    }
}
