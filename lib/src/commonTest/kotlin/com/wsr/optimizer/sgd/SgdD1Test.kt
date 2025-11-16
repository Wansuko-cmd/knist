@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.sgd

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class SgdD1Test {
    @Test
    fun `SgdD1の_adapt=勾配に学習率を乗算した値を返す`() {
        val sgdD1 = SgdD1(rate = 0.1, maxNorm = Float.MAX_VALUE)

        // weight = [10, 20, 30]
        val weight = IOType.d1(listOf(10.0, 20.0, 30.0))
        // dw = [1, 2, 3]
        val dw = IOType.d1(listOf(1.0, 2.0, 3.0))

        // adapt = weight - 0.1 * [1, 2, 3] = [10, 20, 30] - [0.1, 0.2, 0.3] = [9.9, 19.8, 29.7]
        val result = sgdD1.adapt(weight, dw)

        assertEquals(expected = 9.9, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 19.8, actual = result[1], absoluteTolerance = 1e-10)
        assertEquals(expected = 29.7, actual = result[2], absoluteTolerance = 1e-10)
    }
}
