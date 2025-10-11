@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.momentum

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class MomentumD2Test {
    @Test
    fun `MomentumD2の_adapt=初回呼び出し時はSGDと同じ`() {
        val momentumD2 = MomentumD2(rate = 0.1, momentum = 0.9, shape = listOf(2, 2))

        // dw = [[1, 2], [3, 4]]
        val dw = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }

        // 初回: v_0 = 0, adapt(dw) = 0.1 * dw
        val result = momentumD2.adapt(dw)

        assertEquals(expected = 0.1, actual = result[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.2, actual = result[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.3, actual = result[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.4, actual = result[1, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD2の_adapt=2回目以降はvelocityが蓄積される`() {
        val momentumD2 = MomentumD2(rate = 0.1, momentum = 0.9, shape = listOf(2, 2))

        // 1回目
        val dw1 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        momentumD2.adapt(dw1)
        // v_1 = [[1, 2], [3, 4]]

        // 2回目
        val dw2 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }

        // v_2 = 0.9 * [[1, 2], [3, 4]] + [[1, 2], [3, 4]]
        //     = [[0.9, 1.8], [2.7, 3.6]] + [[1, 2], [3, 4]]
        //     = [[1.9, 3.8], [5.7, 7.6]]
        // adapt = 0.1 * v_2 = [[0.19, 0.38], [0.57, 0.76]]
        val result = momentumD2.adapt(dw2)

        assertEquals(expected = 0.19, actual = result[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.38, actual = result[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.57, actual = result[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.76, actual = result[1, 1], absoluteTolerance = 1e-10)
    }
}
