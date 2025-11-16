@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.sgd

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class SgdD1Test {
    @Test
    fun `SgdD1の_adapt=勾配に学習率を乗算した値を返す`() {
        val sgdD1 = SgdD1(rate = 0.1f, maxNorm = Float.MAX_VALUE)

        // weight = [10, 20, 30]
        val weight = IOType.d1(listOf(10.0f, 20.0f, 30.0f))
        // dw = [1, 2, 3]
        val dw = IOType.d1(listOf(1.0f, 2.0f, 3.0f))

        // adapt = weight - 0.1f * [1, 2, 3] = [10, 20, 30] - [0.1f, 0.2f, 0.3f] = [9.9f, 19.8f, 29.7f]
        val result = sgdD1.adapt(weight, dw)

        assertEquals(expected = 9.9f, actual = result[0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 19.8f, actual = result[1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 29.7f, actual = result[2], absoluteTolerance = 1e-6f)
    }
}
