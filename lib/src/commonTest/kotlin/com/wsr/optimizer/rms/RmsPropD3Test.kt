@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.rms

import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.set
import kotlin.test.Test
import kotlin.test.assertEquals

class RmsPropD3Test {
    @Test
    fun `RmsPropD3の_adapt=初回呼び出し時の動作`() {
        val rmsPropD3 = RmsPropD3(rate = 0.1f, rms = 0.9f, maxNorm = Float.MAX_VALUE, shape = listOf(1, 1, 2))

        // weight = [[[10, 20]]]
        val weight = IOType.d3(1, 1, 2) { _, _, z -> (z + 1) * 10.0f }
        // dw = [[[1, 2]]]
        val dw = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() }

        val result = rmsPropD3.adapt(weight, dw)

        // RMSPropは適応的に学習率を調整する
        assertEquals(expected = 9.6838f, actual = result[0, 0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 19.6838f, actual = result[0, 0, 1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `RmsPropD3の_adapt=2回目以降はvelocityが蓄積される`() {
        val rmsPropD3 = RmsPropD3(rate = 0.1f, rms = 0.9f, maxNorm = Float.MAX_VALUE, shape = listOf(1, 1, 2))

        // 1回目
        var weight = IOType.d3(1, 1, 2) { _, _, _ -> 10.0f }
        val dw1 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() }
        val result1 = rmsPropD3.adapt(weight, dw1)

        assertEquals(expected = 9.6838f, actual = result1[0, 0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 9.6838f, actual = result1[0, 0, 1], absoluteTolerance = 1e-4f)

        // 2回目
        weight = result1
        val dw2 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toFloat() }
        val result2 = rmsPropD3.adapt(weight, dw2)

        // velocityが蓄積されるため、更新量の傾向が変わる
        assertEquals(expected = 9.4543f, actual = result2[0, 0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 9.4543f, actual = result2[0, 0, 1], absoluteTolerance = 1e-4f)
    }
}
