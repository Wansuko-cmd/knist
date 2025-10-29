@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.momentum

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class MomentumD1Test {
    @Test
    fun `MomentumD1の_adapt=初回呼び出し時はSGDと同じ`() {
        val momentumD1 = MomentumD1(rate = 0.1, momentum = 0.9, maxNorm = Double.MAX_VALUE, shape = listOf(3))

        // weight = [10, 20, 30]
        val weight = IOType.d1(listOf(10.0, 20.0, 30.0))
        // dw = [1, 2, 3]
        val dw = IOType.d1(listOf(1.0, 2.0, 3.0))

        // 初回: v_0 = 0.9 * 0 + dw = dw
        // adapt = weight - 0.1 * dw = [10, 20, 30] - [0.1, 0.2, 0.3] = [9.9, 19.8, 29.7]
        val result = momentumD1.adapt(weight, dw)

        assertEquals(expected = 9.9, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 19.8, actual = result[1], absoluteTolerance = 1e-10)
        assertEquals(expected = 29.7, actual = result[2], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD1の_adapt=2回目以降はvelocityが蓄積される`() {
        val momentumD1 = MomentumD1(rate = 0.1, momentum = 0.9, maxNorm = Double.MAX_VALUE, shape = listOf(3))

        // 1回目: dw = [1, 2, 3]
        var weight = IOType.d1(listOf(10.0, 20.0, 30.0))
        val dw1 = IOType.d1(listOf(1.0, 2.0, 3.0))
        weight = momentumD1.adapt(weight, dw1)
        // v_1 = 0.9 * 0 + [1, 2, 3] = [1, 2, 3]
        // weight = [10, 20, 30] - [0.1, 0.2, 0.3] = [9.9, 19.8, 29.7]

        // 2回目: dw = [1, 2, 3]
        val dw2 = IOType.d1(listOf(1.0, 2.0, 3.0))

        // v_2 = 0.9 * [1, 2, 3] + [1, 2, 3] = [0.9, 1.8, 2.7] + [1, 2, 3] = [1.9, 3.8, 5.7]
        // adapt = [9.9, 19.8, 29.7] - 0.1 * [1.9, 3.8, 5.7] = [9.9, 19.8, 29.7] - [0.19, 0.38, 0.57] = [9.71, 19.42, 29.13]
        val result = momentumD1.adapt(weight, dw2)

        assertEquals(expected = 9.71, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 19.42, actual = result[1], absoluteTolerance = 1e-10)
        assertEquals(expected = 29.13, actual = result[2], absoluteTolerance = 1e-10)
    }
}
