@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.momentum

import com.wsr.IOType
import com.wsr.d2
import com.wsr.get
import com.wsr.set
import kotlin.test.Test
import kotlin.test.assertEquals

class MomentumD2Test {
    @Test
    fun `MomentumD2の_adapt=初回呼び出し時はSGDと同じ`() {
        val momentumD2 = MomentumD2(rate = 0.1f, momentum = 0.9f, maxNorm = Float.MAX_VALUE, shape = listOf(2, 2))

        // weight = [[10, 20], [30, 40]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 20 + y * 10 + 10).toFloat() }
        // dw = [[1, 2], [3, 4]]
        val dw = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }

        // 初回: v_0 = 0, adapt = weight - 0.1f * dw
        val result = momentumD2.adapt(weight, dw)

        assertEquals(expected = 9.9f, actual = result[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 19.8f, actual = result[0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 29.7f, actual = result[1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 39.6f, actual = result[1, 1], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `MomentumD2の_adapt=2回目以降はvelocityが蓄積される`() {
        val momentumD2 = MomentumD2(rate = 0.1f, momentum = 0.9f, maxNorm = Float.MAX_VALUE, shape = listOf(2, 2))

        // 1回目
        var weight = IOType.d2(2, 2) { x, y -> (x * 20 + y * 10 + 10).toFloat() }
        val dw1 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        weight = momentumD2.adapt(weight, dw1)
        // v_1 = [[1, 2], [3, 4]]
        // weight = [[10, 20], [30, 40]] - [[0.1f, 0.2f], [0.3f, 0.4f]] = [[9.9f, 19.8f], [29.7f, 39.6f]]

        // 2回目
        val dw2 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }

        // v_2 = 0.9f * [[1, 2], [3, 4]] + [[1, 2], [3, 4]]
        //     = [[0.9f, 1.8f], [2.7f, 3.6f]] + [[1, 2], [3, 4]]
        //     = [[1.9f, 3.8f], [5.7f, 7.6f]]
        // adapt = [[9.9f, 19.8f], [29.7f, 39.6f]] - 0.1f * v_2 = [[9.9f, 19.8f], [29.7f, 39.6f]] - [[0.19f, 0.38f], [0.57f, 0.76f]]
        //       = [[9.71f, 19.42f], [29.13f, 38.84f]]
        val result = momentumD2.adapt(weight, dw2)

        assertEquals(expected = 9.71f, actual = result[0, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 19.42f, actual = result[0, 1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 29.13f, actual = result[1, 0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 38.84f, actual = result[1, 1], absoluteTolerance = 1e-5f)
    }
}
