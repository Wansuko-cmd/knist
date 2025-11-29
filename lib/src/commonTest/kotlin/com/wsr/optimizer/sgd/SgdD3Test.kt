@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.sgd

import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.set
import com.wsr.optimizer.Scheduler
import kotlin.test.Test
import kotlin.test.assertEquals

class SgdD3Test {
    @Test
    fun `SgdD3の_adapt=勾配に学習率を乗算した値を返す`() {
        val sgdD3 = SgdD3(scheduler = Scheduler.Fix(0.1f), maxNorm = Float.MAX_VALUE)

        // weight = [[[10, 20], [30, 40]], [[50, 60], [70, 80]]]
        val weight = IOType.d3(2, 2, 2) { x, y, z -> (x * 40 + y * 20 + z * 10 + 10).toFloat() }
        // dw = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val dw = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }

        // adapt = weight - 0.1f * dw
        val result = sgdD3.adapt(weight = weight, dw = dw)

        assertEquals(expected = 9.9f, actual = result[0, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 19.8f, actual = result[0, 0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 29.7f, actual = result[0, 1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 39.6f, actual = result[0, 1, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 49.5f, actual = result[1, 0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 59.4f, actual = result[1, 0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 69.3f, actual = result[1, 1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 79.2f, actual = result[1, 1, 1], absoluteTolerance = 1e-6f)
    }
}
