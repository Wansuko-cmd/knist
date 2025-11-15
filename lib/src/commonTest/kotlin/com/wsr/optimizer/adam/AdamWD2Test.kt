@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class AdamWD2Test {
    @Test
    fun `AdamWD2の_adapt=初回呼び出し時の動作`() {
        val adamWD2 =
            AdamWD2(
                rate = 0.001,
                momentum = 0.9,
                rms = 0.999,
                decay = 0.01,
                maxNorm = Double.MAX_VALUE,
                shape = listOf(1, 2),
            )

        // weight = [[10, 20]]
        val weight = IOType.d2(1, 2) { _, y -> (y + 1) * 10.0 }
        // dw = [[1, 2]]
        val dw = IOType.d2(1, 2) { _, y -> (y + 1).toDouble() }

        val result = adamWD2.adapt(weight, dw)

        // AdamWはWeight Decayにより、通常のAdamよりもさらに重みが減少する
        // (1 - 0.001*0.01) * [10, 20] - 0.001 * [1, 2] / sqrt([1, 4])
        // = 0.99999 * [10, 20] - 0.001 * [1, 1]
        // = [9.9989, 19.9988]
        assertEquals(expected = 9.9989, actual = result[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 19.9988, actual = result[0, 1], absoluteTolerance = 1e-4)
    }

    @Test
    fun `AdamWD2の_adapt=2回目以降はモーメントが蓄積される`() {
        val adamWD2 =
            AdamWD2(
                rate = 0.001,
                momentum = 0.9,
                rms = 0.999,
                decay = 0.01,
                maxNorm = Double.MAX_VALUE,
                shape = listOf(2, 2),
            )

        // 1回目
        var weight = IOType.d2(2, 2) { _, _ -> 10.0 }
        val dw1 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val result1 = adamWD2.adapt(weight, dw1)

        // dw1 = [[1, 2], [3, 4]]なので各要素で異なる更新
        // [0,0]: dw=1: (1-0.001*0.01)*10 - 0.001*1/sqrt(1) = 9.99999*10 - 0.001 = 9.9989
        // [0,1]: dw=2: (1-0.001*0.01)*10 - 0.001*2/sqrt(4) = 9.99999*10 - 0.001 = 9.9989
        // [1,0]: dw=3: (1-0.001*0.01)*10 - 0.001*3/sqrt(9) = 9.99999*10 - 0.001 = 9.9989
        // [1,1]: dw=4: (1-0.001*0.01)*10 - 0.001*4/sqrt(16) = 9.99999*10 - 0.001 = 9.9989
        assertEquals(expected = 9.9989, actual = result1[0, 0], absoluteTolerance = 1e-5)
        assertEquals(expected = 9.9989, actual = result1[0, 1], absoluteTolerance = 1e-5)
        assertEquals(expected = 9.9989, actual = result1[1, 0], absoluteTolerance = 1e-5)
        assertEquals(expected = 9.9989, actual = result1[1, 1], absoluteTolerance = 1e-5)

        // 2回目
        weight = result1
        val dw2 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val result2 = adamWD2.adapt(weight, dw2)

        // モーメントが蓄積され、バイアス補正も変化し、Weight Decayも適用される
        // dw2 = [[1, 2], [3, 4]] (dw1と同じ)
        // 各要素で計算:
        // [0,0]: dw=1
        //   m_2 = 0.9 * 0.1 + 0.1 * 1 = 0.19
        //   v_2 = 0.999 * 0.001 + 0.001 * 1 = 0.001999
        //   m̂_2 = 0.19 / (1 - 0.9^2) = 0.19 / 0.19 = 1.0
        //   v̂_2 = 0.001999 / (1 - 0.999^2) = 0.001999 / 0.001999 = 1.0
        //   result = (1 - 0.00001) * 9.9989 - 0.001 * 1 / sqrt(1) = 9.99999 * 9.9989 - 0.001 ≈ 9.9978
        // [0,1]: dw=2
        //   m_2 = 0.9 * 0.2 + 0.1 * 2 = 0.38
        //   v_2 = 0.999 * 0.004 + 0.001 * 4 = 0.007996
        //   m̂_2 = 0.38 / 0.19 = 2.0
        //   v̂_2 = 0.007996 / 0.001999 = 4.0
        //   result = 0.99999 * 9.9989 - 0.001 * 2 / sqrt(4) = 9.99999 * 9.9989 - 0.001 ≈ 9.9978
        // [1,0]: dw=3
        //   m_2 = 0.9 * 0.3 + 0.1 * 3 = 0.57
        //   v_2 = 0.999 * 0.009 + 0.001 * 9 = 0.017991
        //   m̂_2 = 0.57 / 0.19 = 3.0
        //   v̂_2 = 0.017991 / 0.001999 = 9.0
        //   result = 0.99999 * 9.9989 - 0.001 * 3 / sqrt(9) = 9.99999 * 9.9989 - 0.001 ≈ 9.9978
        // [1,1]: dw=4
        //   m_2 = 0.9 * 0.4 + 0.1 * 4 = 0.76
        //   v_2 = 0.999 * 0.016 + 0.001 * 16 = 0.031984
        //   m̂_2 = 0.76 / 0.19 = 4.0
        //   v̂_2 = 0.031984 / 0.001999 = 16.0
        //   result = 0.99999 * 9.9989 - 0.001 * 4 / sqrt(16) = 9.99999 * 9.9989 - 0.001 ≈ 9.9978
        assertEquals(expected = 9.9978, actual = result2[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 9.9978, actual = result2[0, 1], absoluteTolerance = 1e-4)
        assertEquals(expected = 9.9978, actual = result2[1, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 9.9978, actual = result2[1, 1], absoluteTolerance = 1e-4)
    }
}
