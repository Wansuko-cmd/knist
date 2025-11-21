@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.momentum

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

import com.wsr.get

class MomentumD3Test {
    @Test
    fun `MomentumD3の_adapt=初回呼び出し時はSGDと同じ`() {
        val momentumD3 = MomentumD3(rate = 0.1f, momentum = 0.9f, maxNorm = Float.MAX_VALUE, shape = listOf(1, 1, 2))

        // weight = [[[10, 20]]]
        val weight = IOType.d3(1, 1, 2) { _, _, z -> (z + 1) * 10.0f }
        // dw = [[[1, 2]]]
        val dw = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() }

        // 初回: v_0 = 0, adapt = weight - 0.1f * dw
        val result = momentumD3.adapt(weight, dw)

        assertEquals(expected = 9.9f, actual = result[0, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 19.8f, actual = result[0, 0, 1], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `MomentumD3の_adapt=2回目以降はvelocityが蓄積される`() {
        val momentumD3 = MomentumD3(rate = 0.1f, momentum = 0.9f, maxNorm = Float.MAX_VALUE, shape = listOf(1, 1, 2))

        // 1回目
        var weight = IOType.d3(1, 1, 2) { _, _, z -> (z + 1) * 10.0f }
        val dw1 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() }
        weight = momentumD3.adapt(weight, dw1)
        // v_1 = [[[1, 2]]]
        // weight = [[[10, 20]]] - [[[0.1f, 0.2f]]] = [[[9.9f, 19.8f]]]

        // 2回目
        val dw2 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() }

        // v_2 = 0.9f * [[[1, 2]]] + [[[1, 2]]]
        //     = [[[0.9f, 1.8f]]] + [[[1, 2]]]
        //     = [[[1.9f, 3.8f]]]
        // adapt = [[[9.9f, 19.8f]]] - 0.1f * v_2 = [[[9.9f, 19.8f]]] - [[[0.19f, 0.38f]]] = [[[9.71f, 19.42f]]]
        val result = momentumD3.adapt(weight, dw2)

        assertEquals(expected = 9.71f, actual = result[0, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 19.42f, actual = result[0, 0, 1], absoluteTolerance = 1e-6f)
    }
}
