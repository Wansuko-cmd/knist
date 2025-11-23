@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.core.set
import kotlin.test.Test
import kotlin.test.assertEquals

class AdamD1Test {
    @Test
    fun `AdamD1の_adapt=初回呼び出し時の動作`() {
        val adamD1 = AdamD1(rate = 0.001f, momentum = 0.9f, rms = 0.999f, maxNorm = Float.MAX_VALUE, shape = listOf(2))

        // weight = [10.0f, 20.0f]
        val weight = IOType.d1(listOf(10.0f, 20.0f))
        // dw = [1, 2]
        val dw = IOType.d1(listOf(1.0f, 2.0f))

        // 初回: t=1
        // m_1 = 0.9f * 0 + 0.1f * [1, 2] = [0.1f, 0.2f]
        // v_1 = 0.999f * 0 + 0.001f * [1, 4] = [0.001f, 0.004f]
        // m̂_1 = [0.1f, 0.2f] / (1 - 0.9f^1) = [0.1f, 0.2f] / 0.1f = [1, 2]
        // v̂_1 = [0.001f, 0.004f] / (1 - 0.999f^1) = [0.001f, 0.004f] / 0.001f = [1, 4]
        // adapt = weight - 0.001f * [1, 2] / (sqrt([1, 4]) + 1e-8)
        val result = adamD1.adapt(weight, dw)

        // Adamは適応的に学習率を調整し、バイアス補正により初期ステップでも安定
        assertEquals(expected = 9.999f, actual = result[0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 19.999f, actual = result[1], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `AdamD1の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamD1 = AdamD1(rate = 0.001f, momentum = 0.9f, rms = 0.999f, maxNorm = Float.MAX_VALUE, shape = listOf(2))

        // 1回目
        var weight = IOType.d1(listOf(10.0f, 10.0f))
        val dw1 = IOType.d1(listOf(1.0f, 1.0f))
        val result1 = adamD1.adapt(weight, dw1)

        assertEquals(expected = 9.999f, actual = result1[0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 9.999f, actual = result1[1], absoluteTolerance = 1e-6f)

        // 2回目
        weight = result1
        val dw2 = IOType.d1(listOf(1.0f, 1.0f))
        val result2 = adamD1.adapt(weight, dw2)

        // モーメントが蓄積され、バイアス補正も変化する
        assertEquals(expected = 9.998f, actual = result2[0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 9.998f, actual = result2[1], absoluteTolerance = 1e-6f)
    }
}
