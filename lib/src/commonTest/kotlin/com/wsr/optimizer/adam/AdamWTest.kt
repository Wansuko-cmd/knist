@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import com.wsr.get
import kotlin.test.Test

class AdamWTest {
    @Test
    fun `AdamWの_d1=AdamWD1インスタンスを返す`() {
        val adamW = AdamW(rate = 0.001f, momentum = 0.9f, rms = 0.999f, decay = 0.01f)
        val adamWD1 = adamW.d1(2)

        // AdamWD1インスタンスが返されることを確認
        assert(adamWD1 is AdamWD1)
    }

    @Test
    fun `AdamWの_d2=AdamWD2インスタンスを返す`() {
        val adamW = AdamW(rate = 0.001f, momentum = 0.9f, rms = 0.999f, decay = 0.01f)
        val adamWD2 = adamW.d2(2, 2)

        // AdamWD2インスタンスが返されることを確認
        assert(adamWD2 is AdamWD2)
    }

    @Test
    fun `AdamWの_d3=AdamWD3インスタンスを返す`() {
        val adamW = AdamW(rate = 0.001f, momentum = 0.9f, rms = 0.999f, decay = 0.01f)
        val adamWD3 = adamW.d3(2, 2, 2)

        // AdamWD3インスタンスが返されることを確認
        assert(adamWD3 is AdamWD3)
    }
}
