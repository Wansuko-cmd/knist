@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.momentum

import com.wsr.optimizer.Scheduler
import kotlin.test.Test

class MomentumTest {
    @Test
    fun `Momentumの_d1=MomentumD1インスタンスを返す`() {
        val momentum = Momentum(scheduler = Scheduler.Fix(0.1f), momentum = 0.9f)
        val momentumD1 = momentum.d1(size = 1)

        // MomentumD1インスタンスが返されることを確認
        assert(momentumD1 is MomentumD1)
    }

    @Test
    fun `Momentumの_d2=MomentumD2インスタンスを返す`() {
        val momentum = Momentum(scheduler = Scheduler.Fix(0.1f), momentum = 0.9f)
        val momentumD2 = momentum.d2(i = 1, j = 1)

        // MomentumD2インスタンスが返されることを確認
        assert(momentumD2 is MomentumD2)
    }

    @Test
    fun `Momentumの_d3=MomentumD3インスタンスを返す`() {
        val momentum = Momentum(scheduler = Scheduler.Fix(0.1f), momentum = 0.9f)
        val momentumD3 = momentum.d3(i = 1, j = 1, k = 1)

        // MomentumD3インスタンスが返されることを確認
        assert(momentumD3 is MomentumD3)
    }
}
