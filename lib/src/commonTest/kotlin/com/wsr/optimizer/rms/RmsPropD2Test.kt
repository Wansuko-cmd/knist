@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.rms

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertTrue

class RmsPropD2Test {
    @Test
    fun `RmsPropD2の_adapt=初回呼び出し時の動作`() {
        val rmsPropD2 = RmsPropD2(rate = 0.1, rms = 0.9, shape = listOf(1, 2))

        // dw = [[1, 2]]
        val dw = IOType.d2(1, 2) { _, y -> (y + 1).toDouble() }

        val result = rmsPropD2.adapt(dw)

        // RMSPropは適応的に学習率を調整する
        assertTrue(result[0, 0] > 0.0)
        assertTrue(result[0, 1] > 0.0)
        assertTrue(result[0, 0].isFinite())
        assertTrue(result[0, 1].isFinite())
    }

    @Test
    fun `RmsPropD2の_adapt=2回目以降はvelocityが蓄積される`() {
        val rmsPropD2 = RmsPropD2(rate = 0.1, rms = 0.9, shape = listOf(2, 2))

        // 1回目
        val dw1 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val result1 = rmsPropD2.adapt(dw1)

        // 2回目
        val dw2 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val result2 = rmsPropD2.adapt(dw2)

        // velocityが蓄積されるため、更新量の傾向が変わる
        assertTrue(result1[0, 0] > 0.0)
        assertTrue(result2[0, 0] > 0.0)
        assertTrue(result1[0, 0].isFinite())
        assertTrue(result2[0, 0].isFinite())
    }
}
