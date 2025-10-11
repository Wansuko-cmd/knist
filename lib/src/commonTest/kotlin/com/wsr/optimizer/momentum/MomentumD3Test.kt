@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.momentum

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class MomentumD3Test {
    @Test
    fun `MomentumD3の_adapt=初回呼び出し時はSGDと同じ`() {
        val momentumD3 = MomentumD3(rate = 0.1, momentum = 0.9, shape = listOf(1, 1, 2))

        // weight = [[[10, 20]]]
        val weight = IOType.d3(1, 1, 2) { _, _, z -> (z + 1) * 10.0 }
        // dw = [[[1, 2]]]
        val dw = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }

        // 初回: v_0 = 0, adapt = weight - 0.1 * dw
        val result = momentumD3.adapt(weight, dw)

        assertEquals(expected = 9.9, actual = result[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 19.8, actual = result[0, 0, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD3の_adapt=2回目以降はvelocityが蓄積される`() {
        val momentumD3 = MomentumD3(rate = 0.1, momentum = 0.9, shape = listOf(1, 1, 2))

        // 1回目
        var weight = IOType.d3(1, 1, 2) { _, _, z -> (z + 1) * 10.0 }
        val dw1 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }
        weight = momentumD3.adapt(weight, dw1)
        // v_1 = [[[1, 2]]]
        // weight = [[[10, 20]]] - [[[0.1, 0.2]]] = [[[9.9, 19.8]]]

        // 2回目
        val dw2 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }

        // v_2 = 0.9 * [[[1, 2]]] + [[[1, 2]]]
        //     = [[[0.9, 1.8]]] + [[[1, 2]]]
        //     = [[[1.9, 3.8]]]
        // adapt = [[[9.9, 19.8]]] - 0.1 * v_2 = [[[9.9, 19.8]]] - [[[0.19, 0.38]]] = [[[9.71, 19.42]]]
        val result = momentumD3.adapt(weight, dw2)

        assertEquals(expected = 9.71, actual = result[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 19.42, actual = result[0, 0, 1], absoluteTolerance = 1e-10)
    }
}
