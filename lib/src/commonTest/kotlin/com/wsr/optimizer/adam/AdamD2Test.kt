@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class AdamD2Test {
    @Test
    fun `AdamD2の_adapt=初回呼び出し時の動作`() {
        val adamD2 = AdamD2(rate = 0.001, momentum = 0.9, rms = 0.999, maxNorm = Double.MAX_VALUE, shape = listOf(1, 2))

        // weight = [[10, 20]]
        val weight = IOType.d2(1, 2) { _, y -> (y + 1) * 10.0 }
        // dw = [[1, 2]]
        val dw = IOType.d2(1, 2) { _, y -> (y + 1).toDouble() }

        val result = adamD2.adapt(weight, dw)

        // Adamは適応的に学習率を調整し、バイアス補正により初期ステップでも安定
        assertEquals(expected = 9.999, actual = result[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 19.999, actual = result[0, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `AdamD2の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamD2 = AdamD2(rate = 0.001, momentum = 0.9, rms = 0.999, maxNorm = Double.MAX_VALUE, shape = listOf(2, 2))

        // 1回目
        var weight = IOType.d2(2, 2) { _, _ -> 10.0 }
        val dw1 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val result1 = adamD2.adapt(weight, dw1)

        assertEquals(expected = 9.999, actual = result1[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.999, actual = result1[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.999, actual = result1[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.999, actual = result1[1, 1], absoluteTolerance = 1e-10)

        // 2回目
        weight = result1
        val dw2 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val result2 = adamD2.adapt(weight, dw2)

        // モーメントが蓄積され、バイアス補正も変化する
        assertEquals(expected = 9.998, actual = result2[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.998, actual = result2[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.998, actual = result2[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.998, actual = result2[1, 1], absoluteTolerance = 1e-10)
    }
}
