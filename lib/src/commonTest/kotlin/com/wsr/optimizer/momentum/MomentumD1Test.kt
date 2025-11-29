@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.momentum

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.core.set
import com.wsr.optimizer.Scheduler
import kotlin.test.Test
import kotlin.test.assertEquals

class MomentumD1Test {
    @Test
    fun `MomentumD1の_adapt=初回呼び出し時はSGDと同じ`() {
        val momentumD1 =
            MomentumD1(
                scheduler = Scheduler.Fix(0.1f),
                momentum = 0.9f,
                maxNorm = Float.MAX_VALUE,
                stepUnit = 1,
                shape = listOf(3),
            )

        // weight = [10, 20, 30]
        val weight = IOType.d1(listOf(10.0f, 20.0f, 30.0f))
        // dw = [1, 2, 3]
        val dw = IOType.d1(listOf(1.0f, 2.0f, 3.0f))

        // 初回: v_0 = 0.9f * 0 + dw = dw
        // adapt = weight - 0.1f * dw = [10, 20, 30] - [0.1f, 0.2f, 0.3f] = [9.9f, 19.8f, 29.7f]
        val result = momentumD1.adapt(weight, dw)

        assertEquals(expected = 9.9f, actual = result[0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 19.8f, actual = result[1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 29.7f, actual = result[2], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `MomentumD1の_adapt=2回目以降はvelocityが蓄積される`() {
        val momentumD1 =
            MomentumD1(
                scheduler = Scheduler.Fix(0.1f),
                momentum = 0.9f,
                maxNorm = Float.MAX_VALUE,
                stepUnit = 1,
                shape = listOf(3),
            )

        // 1回目: dw = [1, 2, 3]
        var weight = IOType.d1(listOf(10.0f, 20.0f, 30.0f))
        val dw1 = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        weight = momentumD1.adapt(weight, dw1)
        // v_1 = 0.9f * 0 + [1, 2, 3] = [1, 2, 3]
        // weight = [10, 20, 30] - [0.1f, 0.2f, 0.3f] = [9.9f, 19.8f, 29.7f]

        // 2回目: dw = [1, 2, 3]
        val dw2 = IOType.d1(listOf(1.0f, 2.0f, 3.0f))

        // v_2 = 0.9f * [1, 2, 3] + [1, 2, 3] = [0.9f, 1.8f, 2.7f] + [1, 2, 3] = [1.9f, 3.8f, 5.7f]
        // adapt = [9.9f, 19.8f, 29.7f] - 0.1f * [1.9f, 3.8f, 5.7f] = [9.9f, 19.8f, 29.7f] - [0.19f, 0.38f, 0.57f] = [9.71f, 19.42f, 29.13f]
        val result = momentumD1.adapt(weight, dw2)

        assertEquals(expected = 9.71f, actual = result[0], absoluteTolerance = 1e-5f)
        assertEquals(expected = 19.42f, actual = result[1], absoluteTolerance = 1e-5f)
        assertEquals(expected = 29.13f, actual = result[2], absoluteTolerance = 1e-5f)
    }
}
