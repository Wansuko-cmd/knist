@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.sgd

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class SgdD2Test {
    @Test
    fun `SgdD2の_adapt=勾配に学習率を乗算した値を返す`() {
        val sgdD2 = SgdD2(rate = 0.1)

        // dw = [[1, 2], [3, 4]]
        val dw = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }

        // adapt(dw) = 0.1 * [[1, 2], [3, 4]] = [[0.1, 0.2], [0.3, 0.4]]
        val result = sgdD2.adapt(dw)

        assertEquals(expected = 0.1, actual = result[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.2, actual = result[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.3, actual = result[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.4, actual = result[1, 1], absoluteTolerance = 1e-10)
    }
}
