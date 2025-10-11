@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.sgd

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class SgdTest {
    @Test
    fun `Sgdの_d1=SgdD1インスタンスを返す`() {
        val sgd = Sgd(rate = 0.1)
        val sgdD1 = sgd.d1()

        // SgdD1インスタンスが返されることを確認
        assert(sgdD1 is SgdD1)
    }

    @Test
    fun `Sgdの_d2=SgdD2インスタンスを返す`() {
        val sgd = Sgd(rate = 0.1)
        val sgdD2 = sgd.d2()

        // SgdD2インスタンスが返されることを確認
        assert(sgdD2 is SgdD2)
    }

    @Test
    fun `Sgdの_d3=SgdD3インスタンスを返す`() {
        val sgd = Sgd(rate = 0.1)
        val sgdD3 = sgd.d3()

        // SgdD3インスタンスが返されることを確認
        assert(sgdD3 is SgdD3)
    }

    @Test
    fun `SgdD1の_adapt=勾配に学習率を乗算した値を返す`() {
        val sgdD1 = SgdD1(rate = 0.1)

        // dw = [1, 2, 3]
        val dw = IOType.d1(listOf(1.0, 2.0, 3.0))

        // adapt(dw) = 0.1 * [1, 2, 3] = [0.1, 0.2, 0.3]
        val result = sgdD1.adapt(dw)

        assertEquals(expected = 0.1, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.2, actual = result[1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.3, actual = result[2], absoluteTolerance = 1e-10)
    }

    @Test
    fun `SgdD2の_adapt=勾配に学習率を乗算した値を返す`() {
        val sgdD2 = SgdD2(rate = 0.1)

        // dw = [[1, 2], [3, 4]]
        val dw = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }

        // adapt(dw) = 0.1 * [[1, 2], [3, 4]] = [[0.1, 0.2], [0.3, 0.4]]
        val result = sgdD2.adapt(dw)

        assertEquals(expected = 0.1, actual = result[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.2, actual = result[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.3, actual = result[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.4, actual = result[1, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `SgdD3の_adapt=勾配に学習率を乗算した値を返す`() {
        val sgdD3 = SgdD3(rate = 0.1)

        // dw = [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val dw = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() }

        // adapt(dw) = 0.1 * dw
        val result = sgdD3.adapt(dw)

        assertEquals(expected = 0.1, actual = result[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.2, actual = result[0, 0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.3, actual = result[0, 1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.4, actual = result[0, 1, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.5, actual = result[1, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.6, actual = result[1, 0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.7, actual = result[1, 1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.8, actual = result[1, 1, 1], absoluteTolerance = 1e-10)
    }
}
