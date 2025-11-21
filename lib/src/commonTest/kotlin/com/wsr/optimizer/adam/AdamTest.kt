@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.adam

import kotlin.test.Test

import com.wsr.get

class AdamTest {
    @Test
    fun `Adamの_d1=AdamD1インスタンスを返す`() {
        val adam = Adam(rate = 0.001f, momentum = 0.9f, rms = 0.999f)
        val adamD1 = adam.d1(size = 1)

        // AdamD1インスタンスが返されることを確認
        assert(adamD1 is AdamD1)
    }

    @Test
    fun `Adamの_d2=AdamD2インスタンスを返す`() {
        val adam = Adam(rate = 0.001f, momentum = 0.9f, rms = 0.999f)
        val adamD2 = adam.d2(x = 1, y = 1)

        // AdamD2インスタンスが返されることを確認
        assert(adamD2 is AdamD2)
    }

    @Test
    fun `Adamの_d3=AdamD3インスタンスを返す`() {
        val adam = Adam(rate = 0.001f, momentum = 0.9f, rms = 0.999f)
        val adamD3 = adam.d3(x = 1, y = 1, z = 1)

        // AdamD3インスタンスが返されることを確認
        assert(adamD3 is AdamD3)
    }
}
