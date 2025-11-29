@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.rms

import com.wsr.optimizer.Scheduler
import kotlin.test.Test

class RmsPropTest {
    @Test
    fun `RmsPropの_d1=RmsPropD1インスタンスを返す`() {
        val rmsProp = RmsProp(scheduler = Scheduler.Fix(0.001f), rms = 0.9f)
        val rmsPropD1 = rmsProp.d1(size = 1)

        // RmsPropD1インスタンスが返されることを確認
        assert(rmsPropD1 is RmsPropD1)
    }

    @Test
    fun `RmsPropの_d2=RmsPropD2インスタンスを返す`() {
        val rmsProp = RmsProp(scheduler = Scheduler.Fix(0.001f), rms = 0.9f)
        val rmsPropD2 = rmsProp.d2(i = 1, j = 1)

        // RmsPropD2インスタンスが返されることを確認
        assert(rmsPropD2 is RmsPropD2)
    }

    @Test
    fun `RmsPropの_d3=RmsPropD3インスタンスを返す`() {
        val rmsProp = RmsProp(scheduler = Scheduler.Fix(0.001f), rms = 0.9f)
        val rmsPropD3 = rmsProp.d3(i = 1, j = 1, k = 1)

        // RmsPropD3インスタンスが返されることを確認
        assert(rmsPropD3 is RmsPropD3)
    }
}
