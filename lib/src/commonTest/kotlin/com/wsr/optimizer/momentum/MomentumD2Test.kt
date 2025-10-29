@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.momentum

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class MomentumD2Test {
    @Test
    fun `MomentumD2の_adapt=初回呼び出し時はSGDと同じ`() {
        val momentumD2 = MomentumD2(rate = 0.1, momentum = 0.9, maxNorm = Double.MAX_VALUE, shape = listOf(2, 2))

        // weight = [[10, 20], [30, 40]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 20 + y * 10 + 10).toDouble() }
        // dw = [[1, 2], [3, 4]]
        val dw = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }

        // 初回: v_0 = 0, adapt = weight - 0.1 * dw
        val result = momentumD2.adapt(weight, dw)

        assertEquals(expected = 9.9, actual = result[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 19.8, actual = result[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 29.7, actual = result[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 39.6, actual = result[1, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD2の_adapt=2回目以降はvelocityが蓄積される`() {
        val momentumD2 = MomentumD2(rate = 0.1, momentum = 0.9, maxNorm = Double.MAX_VALUE, shape = listOf(2, 2))

        // 1回目
        var weight = IOType.d2(2, 2) { x, y -> (x * 20 + y * 10 + 10).toDouble() }
        val dw1 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        weight = momentumD2.adapt(weight, dw1)
        // v_1 = [[1, 2], [3, 4]]
        // weight = [[10, 20], [30, 40]] - [[0.1, 0.2], [0.3, 0.4]] = [[9.9, 19.8], [29.7, 39.6]]

        // 2回目
        val dw2 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }

        // v_2 = 0.9 * [[1, 2], [3, 4]] + [[1, 2], [3, 4]]
        //     = [[0.9, 1.8], [2.7, 3.6]] + [[1, 2], [3, 4]]
        //     = [[1.9, 3.8], [5.7, 7.6]]
        // adapt = [[9.9, 19.8], [29.7, 39.6]] - 0.1 * v_2 = [[9.9, 19.8], [29.7, 39.6]] - [[0.19, 0.38], [0.57, 0.76]]
        //       = [[9.71, 19.42], [29.13, 38.84]]
        val result = momentumD2.adapt(weight, dw2)

        assertEquals(expected = 9.71, actual = result[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 19.42, actual = result[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 29.13, actual = result[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 38.84, actual = result[1, 1], absoluteTolerance = 1e-10)
    }
}
