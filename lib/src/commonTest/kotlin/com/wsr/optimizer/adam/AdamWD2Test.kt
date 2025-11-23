@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.core.set
import kotlin.test.Test
import kotlin.test.assertEquals

class AdamWD2Test {
    @Test
    fun `AdamWD2の_adapt=初回呼び出し時の動作`() {
        val adamWD2 =
            AdamWD2(
                rate = 0.001f,
                momentum = 0.9f,
                rms = 0.999f,
                decay = 0.01f,
                maxNorm = Float.MAX_VALUE,
                shape = listOf(1, 2),
            )

        // weight = [[10, 20]]
        val weight = IOType.d2(1, 2) { _, y -> (y + 1) * 10.0f }
        // dw = [[1, 2]]
        val dw = IOType.d2(1, 2) { _, y -> (y + 1).toFloat() }

        val result = adamWD2.adapt(weight, dw)

        // AdamWはWeight Decayにより、通常のAdamよりもさらに重みが減少する
        // (1 - 0.001f*0.01f) * [10, 20] - 0.001f * [1, 2] / sqrt([1, 4])
        // = 0.99999f * [10, 20] - 0.001f * [1, 1]
        // = [9.9989f, 19.9988f]
        assertEquals(expected = 9.9989f, actual = result[0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 19.9988f, actual = result[0, 1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `AdamWD2の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamWD2 =
            AdamWD2(
                rate = 0.001f,
                momentum = 0.9f,
                rms = 0.999f,
                decay = 0.01f,
                maxNorm = Float.MAX_VALUE,
                shape = listOf(2, 2),
            )

        // 1回目
        var weight = IOType.d2(2, 2) { _, _ -> 10.0f }
        val dw1 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val result1 = adamWD2.adapt(weight, dw1)

        // dw1 = [[1, 2], [3, 4]]なので各要素で異なる更新
        // [0,0]: dw=1: (1-0.001f*0.01f)*10 - 0.001f*1/sqrt(1) = 9.99999f*10 - 0.001f = 9.9989f
        // [0,1]: dw=2: (1-0.001f*0.01f)*10 - 0.001f*2/sqrt(4) = 9.99999f*10 - 0.001f = 9.9989f
        // [1,0]: dw=3: (1-0.001f*0.01f)*10 - 0.001f*3/sqrt(9) = 9.99999f*10 - 0.001f = 9.9989f
        // [1,1]: dw=4: (1-0.001f*0.01f)*10 - 0.001f*4/sqrt(16) = 9.99999f*10 - 0.001f = 9.9989f
        assertEquals(expected = 9.9989f, actual = result1[0, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 9.9989f, actual = result1[0, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 9.9989f, actual = result1[1, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 9.9989f, actual = result1[1, 1], absoluteTolerance = 1e-5f)

        // 2回目
        weight = result1
        val dw2 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val result2 = adamWD2.adapt(weight, dw2)

        // モーメントが蓄積され、バイアス補正も変化し、Weight Decayも適用される
        // dw2 = [[1, 2], [3, 4]] (dw1と同じ)
        // 各要素で計算:
        // [0,0]: dw=1
        //   m_2 = 0.9f * 0.1f + 0.1f * 1 = 0.19f
        //   v_2 = 0.999f * 0.001f + 0.001f * 1 = 0.001999f
        //   m̂_2 = 0.19f / (1 - 0.9f^2) = 0.19f / 0.19f = 1.0f
        //   v̂_2 = 0.001999f / (1 - 0.999f^2) = 0.001999f / 0.001999f = 1.0f
        //   result = (1 - 0.00001f) * 9.9989f - 0.001f * 1 / sqrt(1) = 9.99999f * 9.9989f - 0.001f ≈ 9.9978f
        // [0,1]: dw=2
        //   m_2 = 0.9f * 0.2f + 0.1f * 2 = 0.38f
        //   v_2 = 0.999f * 0.004f + 0.001f * 4 = 0.007996f
        //   m̂_2 = 0.38f / 0.19f = 2.0f
        //   v̂_2 = 0.007996f / 0.001999f = 4.0f
        //   result = 0.99999f * 9.9989f - 0.001f * 2 / sqrt(4) = 9.99999f * 9.9989f - 0.001f ≈ 9.9978f
        // [1,0]: dw=3
        //   m_2 = 0.9f * 0.3f + 0.1f * 3 = 0.57f
        //   v_2 = 0.999f * 0.009f + 0.001f * 9 = 0.017991f
        //   m̂_2 = 0.57f / 0.19f = 3.0f
        //   v̂_2 = 0.017991f / 0.001999f = 9.0f
        //   result = 0.99999f * 9.9989f - 0.001f * 3 / sqrt(9) = 9.99999f * 9.9989f - 0.001f ≈ 9.9978f
        // [1,1]: dw=4
        //   m_2 = 0.9f * 0.4f + 0.1f * 4 = 0.76f
        //   v_2 = 0.999f * 0.016f + 0.001f * 16 = 0.031984f
        //   m̂_2 = 0.76f / 0.19f = 4.0f
        //   v̂_2 = 0.031984f / 0.001999f = 16.0f
        //   result = 0.99999f * 9.9989f - 0.001f * 4 / sqrt(16) = 9.99999f * 9.9989f - 0.001f ≈ 9.9978f
        assertEquals(expected = 9.9978f, actual = result2[0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 9.9978f, actual = result2[0, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 9.9978f, actual = result2[1, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 9.9978f, actual = result2[1, 1], absoluteTolerance = 1e-4f)
    }
}
