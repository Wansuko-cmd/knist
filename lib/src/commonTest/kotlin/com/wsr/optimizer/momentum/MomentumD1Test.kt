@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.momentum

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class MomentumD1Test {
    @Test
    fun `MomentumD1の_adapt=初回呼び出し時はSGDと同じ`() {
        val momentumD1 = MomentumD1(rate = 0.1, momentum = 0.9)

        // dw = [1, 2, 3]
        val dw = IOType.d1(listOf(1.0, 2.0, 3.0))

        // 初回: v_0 = 0.9 * 0 + dw = dw
        // adapt(dw) = 0.1 * dw = [0.1, 0.2, 0.3]
        val result = momentumD1.adapt(dw)

        assertEquals(expected = 0.1, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.2, actual = result[1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.3, actual = result[2], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD1の_adapt=2回目以降はvelocityが蓄積される`() {
        val momentumD1 = MomentumD1(rate = 0.1, momentum = 0.9)

        // 1回目: dw = [1, 2, 3]
        val dw1 = IOType.d1(listOf(1.0, 2.0, 3.0))
        momentumD1.adapt(dw1)
        // v_1 = 0.9 * 0 + [1, 2, 3] = [1, 2, 3]

        // 2回目: dw = [1, 2, 3]
        val dw2 = IOType.d1(listOf(1.0, 2.0, 3.0))

        // v_2 = 0.9 * [1, 2, 3] + [1, 2, 3] = [0.9, 1.8, 2.7] + [1, 2, 3] = [1.9, 3.8, 5.7]
        // adapt = 0.1 * [1.9, 3.8, 5.7] = [0.19, 0.38, 0.57]
        val result = momentumD1.adapt(dw2)

        assertEquals(expected = 0.19, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.38, actual = result[1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.57, actual = result[2], absoluteTolerance = 1e-10)
    }
}
