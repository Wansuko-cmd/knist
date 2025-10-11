@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.rms

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertTrue

class RmsPropD1Test {
    @Test
    fun `RmsPropD1の_adapt=初回呼び出し時の動作`() {
        val rmsPropD1 = RmsPropD1(rate = 0.1, rms = 0.9)

        // dw = [1, 2]
        val dw = IOType.d1(listOf(1.0, 2.0))

        // 初回: v_0 = 0
        // v_1 = 0.9 * 0 + 0.1 * [1, 4] = [0.1, 0.4]
        // adapt = 0.1 / (sqrt([0.1, 0.4]) + 1e-8) * [1, 2]
        //       ≈ 0.1 / [0.316, 0.632] * [1, 2]
        //       ≈ [0.316, 0.316] * [1, 2]
        //       ≈ [0.316, 0.632]
        val result = rmsPropD1.adapt(dw)

        // RMSPropは勾配の大きさに応じて学習率を調整するため、
        // 大きな勾配には小さな更新、小さな勾配には大きな更新が適用される
        assertTrue(result[0] > 0.0)
        assertTrue(result[1] > 0.0)
        // 勾配が大きい方（dw[1]=2）は更新量も大きくなる
        assertTrue(result[1] > result[0])
    }

    @Test
    fun `RmsPropD1の_adapt=2回目以降はvelocityが蓄積される`() {
        val rmsPropD1 = RmsPropD1(rate = 0.1, rms = 0.9)

        // 1回目: dw = [1, 1]
        val dw1 = IOType.d1(listOf(1.0, 1.0))
        rmsPropD1.adapt(dw1)
        // v_1 = 0.9 * 0 + 0.1 * [1, 1] = [0.1, 0.1]

        // 2回目: dw = [1, 1]
        val dw2 = IOType.d1(listOf(1.0, 1.0))
        val result = rmsPropD1.adapt(dw2)
        // v_2 = 0.9 * [0.1, 0.1] + 0.1 * [1, 1] = [0.09, 0.09] + [0.1, 0.1] = [0.19, 0.19]
        // adapt = 0.1 / (sqrt([0.19, 0.19]) + 1e-8) * [1, 1]
        //       ≈ 0.1 / [0.436, 0.436] * [1, 1]
        //       ≈ [0.229, 0.229]

        // velocityが蓄積されるため、1回目より更新量が小さくなる
        assertTrue(result[0] > 0.0)
        assertTrue(result[1] > 0.0)
    }
}
