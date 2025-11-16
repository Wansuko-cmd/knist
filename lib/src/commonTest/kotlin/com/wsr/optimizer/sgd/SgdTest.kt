@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.sgd

import kotlin.test.Test

class SgdTest {
    @Test
    fun `Sgdの_d1=SgdD1インスタンスを返す`() {
        val sgd = Sgd(rate = 0.1f)
        val sgdD1 = sgd.d1(size = 1)

        // SgdD1インスタンスが返されることを確認
        assert(sgdD1 is SgdD1)
    }

    @Test
    fun `Sgdの_d2=SgdD2インスタンスを返す`() {
        val sgd = Sgd(rate = 0.1f)
        val sgdD2 = sgd.d2(x = 1, y = 1)

        // SgdD2インスタンスが返されることを確認
        assert(sgdD2 is SgdD2)
    }

    @Test
    fun `Sgdの_d3=SgdD3インスタンスを返す`() {
        val sgd = Sgd(rate = 0.1f)
        val sgdD3 = sgd.d3(x = 1, y = 1, z = 1)

        // SgdD3インスタンスが返されることを確認
        assert(sgdD3 is SgdD3)
    }
}
