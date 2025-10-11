@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.sgd

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class SgdD3Test {
    @Test
    fun `SgdD3の_adapt=勾配に学習率を乗算した値を返す`() {
        val sgdD3 = SgdD3(rate = 0.1)

        // weight = [[[10, 20], [30, 40]], [[50, 60], [70, 80]]]
        val weight = IOType.d3(2, 2, 2) { x, y, z -> (x * 40 + y * 20 + z * 10 + 10).toDouble() }
        // dw = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val dw = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() }

        // adapt = weight - 0.1 * dw
        val result = sgdD3.adapt(weight = weight, dw = dw)

        assertEquals(expected = 9.9, actual = result[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 19.8, actual = result[0, 0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 29.7, actual = result[0, 1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 39.6, actual = result[0, 1, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 49.5, actual = result[1, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 59.4, actual = result[1, 0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 69.3, actual = result[1, 1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 79.2, actual = result[1, 1, 1], absoluteTolerance = 1e-10)
    }
}
