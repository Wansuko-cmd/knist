@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.rms

import com.wsr.IOType
import com.wsr.get
import kotlin.test.Test
import kotlin.test.assertEquals

class RmsPropD1Test {
    @Test
    fun `RmsPropD1の_adapt=初回呼び出し時の動作`() {
        val rmsPropD1 = RmsPropD1(rate = 0.1f, rms = 0.9f, maxNorm = Float.MAX_VALUE, shape = listOf(2))

        // weight = [10, 20]
        val weight = IOType.d1(listOf(10.0f, 20.0f))
        // dw = [1, 2]
        val dw = IOType.d1(listOf(1.0f, 2.0f))

        // 初回: v_0 = 0
        // v_1 = 0.9f * 0 + 0.1f * [1, 4] = [0.1f, 0.4f]
        // adapt = weight - 0.1f / (sqrt([0.1f, 0.4f]) + 1e-8) * [1, 2]
        val result = rmsPropD1.adapt(weight, dw)

        // RMSPropは勾配の大きさに応じて学習率を調整するため、weightより小さくなる
        assertEquals(expected = 9.6838f, actual = result[0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 19.6838f, actual = result[1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `RmsPropD1の_adapt=2回目以降はvelocityが蓄積される`() {
        val rmsPropD1 = RmsPropD1(rate = 0.1f, rms = 0.9f, maxNorm = Float.MAX_VALUE, shape = listOf(2))

        // 1回目: dw = [1, 1]
        var weight = IOType.d1(listOf(10.0f, 10.0f))
        val dw1 = IOType.d1(listOf(1.0f, 1.0f))
        val result1 = rmsPropD1.adapt(weight, dw1)
        // v_1 = 0.9f * 0 + 0.1f * [1, 1] = [0.1f, 0.1f]
        assertEquals(expected = 9.6837f, actual = result1[0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 9.6837f, actual = result1[1], absoluteTolerance = 1e-4f)

        // 2回目: dw = [1, 1]
        weight = result1
        val dw2 = IOType.d1(listOf(1.0f, 1.0f))
        val result2 = rmsPropD1.adapt(weight, dw2)
        // v_2 = 0.9f * [0.1f, 0.1f] + 0.1f * [1, 1] = [0.09f, 0.09f] + [0.1f, 0.1f] = [0.19f, 0.19f]

        // velocityが蓄積されるため、1回目より更新量が変化する
        assertEquals(expected = 9.4544f, actual = result2[0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 9.4544f, actual = result2[1], absoluteTolerance = 1e-4f)
    }
}
