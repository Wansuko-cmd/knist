@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.core.set
import com.wsr.optimizer.Scheduler
import kotlin.test.Test
import kotlin.test.assertEquals

class AdamWD1Test {
    @Test
    fun `AdamWD1の_adapt=初回呼び出し時の動作`() {
        val adamWD1 =
            AdamWD1(
                scheduler = Scheduler.Fix(0.001f),
                momentum = 0.9f,
                rms = 0.999f,
                decay = 0.01f,
                maxNorm = Float.MAX_VALUE,
                shape = listOf(2),
            )

        // weight = [10.0f, 20.0f]
        val weight = IOType.d1(listOf(10.0f, 20.0f))
        // dw = [1, 2]
        val dw = IOType.d1(listOf(1.0f, 2.0f))

        // 初回: t=1
        // m_1 = 0.9f * 0 + 0.1f * [1, 2] = [0.1f, 0.2f]
        // v_1 = 0.999f * 0 + 0.001f * [1, 4] = [0.001f, 0.004f]
        // m̂_1 = [0.1f, 0.2f] / (1 - 0.9f^1) = [0.1f, 0.2f] / 0.1f = [1, 2]
        // v̂_1 = [0.001f, 0.004f] / (1 - 0.999f^1) = [0.001f, 0.004f] / 0.001f = [1, 4]
        // adapt = (1 - 0.001f * 0.01f) * weight - 0.001f * [1, 2] / (sqrt([1, 4]) + 1e-8)
        //       = (1 - 0.00001f) * [10, 20] - 0.001f * [1, 2] / [1, 2]
        //       = 0.99999f * [10, 20] - 0.001f * [1, 1]
        //       = [9.9999f, 19.9998f] - [0.001f, 0.001f]
        //       = [9.9989f, 19.9988f]
        val result = adamWD1.adapt(weight, dw)

        // AdamWはWeight Decayにより、通常のAdamよりもさらに重みが減少する
        assertEquals(expected = 9.9989f, actual = result[0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 19.9988f, actual = result[1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `AdamWD1の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamWD1 =
            AdamWD1(
                scheduler = Scheduler.Fix(0.001f),
                momentum = 0.9f,
                rms = 0.999f,
                decay = 0.01f,
                maxNorm = Float.MAX_VALUE,
                shape = listOf(2),
            )

        // 1回目
        var weight = IOType.d1(listOf(10.0f, 10.0f))
        val dw1 = IOType.d1(listOf(1.0f, 1.0f))
        val result1 = adamWD1.adapt(weight, dw1)

        // 1回目の期待値: (1 - 0.001f*0.01f)*10 - 0.001f*1/sqrt(1) = 9.99999f*10 - 0.001f = 9.9989f
        assertEquals(expected = 9.9989f, actual = result1[0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 9.9989f, actual = result1[1], absoluteTolerance = 1e-4f)

        // 2回目
        weight = result1
        val dw2 = IOType.d1(listOf(1.0f, 1.0f))
        val result2 = adamWD1.adapt(weight, dw2)

        // 2回目: モーメントが蓄積され、バイアス補正も変化し、Weight Decayも適用される
        // m_2 = 0.9f * 0.1f + 0.1f * 1 = 0.19f
        // v_2 = 0.999f * 0.001f + 0.001f * 1 = 0.001999f
        // m̂_2 = 0.19f / (1 - 0.9f^2) = 0.19f / 0.19f = 1.0f
        // v̂_2 = 0.001999f / (1 - 0.999f^2) = 0.001999f / 0.001999f = 1.0f
        // result = (1 - 0.00001f) * 9.9989f - 0.001f * 1 / sqrt(1)
        //        = 0.99999f * 9.9989f - 0.001f ≈ 9.9978f
        assertEquals(expected = 9.9978f, actual = result2[0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 9.9978f, actual = result2[1], absoluteTolerance = 1e-4f)
    }
}
