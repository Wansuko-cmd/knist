@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.sgd

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class SgdD2Test {
    @Test
    fun `SgdD2の_adapt=勾配に学習率を乗算した値を返す`() {
        val sgdD2 = SgdD2(rate = 0.1f, maxNorm = Float.MAX_VALUE)

        // weight = [[10, 20], [30, 40]]
        val weight = IOType.d2(2, 2) { x, y -> (x * 20 + y * 10 + 10).toFloat() }
        // dw = [[1, 2], [3, 4]]
        val dw = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() }

        // adapt = weight - 0.1f * [[1, 2], [3, 4]] = [[10, 20], [30, 40]] - [[0.1f, 0.2f], [0.3f, 0.4f]]
        val result = sgdD2.adapt(weight, dw)

        assertEquals(expected = 9.9f, actual = result[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 19.8f, actual = result[0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 29.7f, actual = result[1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 39.6f, actual = result[1, 1], absoluteTolerance = 1e-6f)
    }
}
