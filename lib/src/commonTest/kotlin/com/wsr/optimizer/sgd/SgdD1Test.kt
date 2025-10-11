@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.sgd

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class SgdD1Test {
    @Test
    fun `SgdD1の_adapt=勾配に学習率を乗算した値を返す`() {
        val sgdD1 = SgdD1(rate = 0.1)

        // dw = [1, 2, 3]
        val dw = IOType.d1(listOf(1.0, 2.0, 3.0))

        // adapt(dw) = 0.1 * [1, 2, 3] = [0.1, 0.2, 0.3]
        val result = sgdD1.adapt(dw)

        assertEquals(expected = 0.1, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.2, actual = result[1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.3, actual = result[2], absoluteTolerance = 1e-10)
    }
}
