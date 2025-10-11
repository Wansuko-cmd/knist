@file:Suppress("NonAsciiCharacters")

package com.wsr.optimizer.momentum

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class MomentumTest {
    @Test
    fun `Momentumの_d1=MomentumD1インスタンスを返す`() {
        val momentum = Momentum(rate = 0.1, momentum = 0.9)
        val momentumD1 = momentum.d1()

        // MomentumD1インスタンスが返されることを確認
        assert(momentumD1 is MomentumD1)
    }

    @Test
    fun `Momentumの_d2=MomentumD2インスタンスを返す`() {
        val momentum = Momentum(rate = 0.1, momentum = 0.9)
        val momentumD2 = momentum.d2()

        // MomentumD2インスタンスが返されることを確認
        assert(momentumD2 is MomentumD2)
    }

    @Test
    fun `Momentumの_d3=MomentumD3インスタンスを返す`() {
        val momentum = Momentum(rate = 0.1, momentum = 0.9)
        val momentumD3 = momentum.d3()

        // MomentumD3インスタンスが返されることを確認
        assert(momentumD3 is MomentumD3)
    }

    @Test
    fun `MomentumD1の_adapt=初回呼び出し時はSGDと同じ`() {
        val momentumD1 = MomentumD1(rate = 0.1, momentum = 0.9)

        // dw = [1, 2, 3]
        val dw = IOType.d1(listOf(1.0, 2.0, 3.0))

        // 初回: v_0 = 0.9 * 0 + dw = dw
        // adapt(dw) = 0.1 * dw = [0.1, 0.2, 0.3]
        val result = momentumD1.adapt(dw)

        assertEquals(expected = 0.1, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.2, actual = result[1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.3, actual = result[2], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD1の_adapt=2回目以降はvelocityが蓄積される`() {
        val momentumD1 = MomentumD1(rate = 0.1, momentum = 0.9)

        // 1回目: dw = [1, 2, 3]
        val dw1 = IOType.d1(listOf(1.0, 2.0, 3.0))
        momentumD1.adapt(dw1)
        // v_1 = 0.9 * 0 + [1, 2, 3] = [1, 2, 3]

        // 2回目: dw = [1, 2, 3]
        val dw2 = IOType.d1(listOf(1.0, 2.0, 3.0))
        val result = momentumD1.adapt(dw2)
        // v_2 = 0.9 * [1, 2, 3] + [1, 2, 3] = [0.9, 1.8, 2.7] + [1, 2, 3] = [1.9, 3.8, 5.7]
        // adapt = 0.1 * [1.9, 3.8, 5.7] = [0.19, 0.38, 0.57]

        assertEquals(expected = 0.19, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.38, actual = result[1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.57, actual = result[2], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD1の_adapt=異なる勾配でvelocityが正しく更新される`() {
        val momentumD1 = MomentumD1(rate = 0.1, momentum = 0.9)

        // 1回目: dw = [10, 0]
        val dw1 = IOType.d1(listOf(10.0, 0.0))
        momentumD1.adapt(dw1)
        // v_1 = [10, 0]

        // 2回目: dw = [0, 10]
        val dw2 = IOType.d1(listOf(0.0, 10.0))
        val result = momentumD1.adapt(dw2)
        // v_2 = 0.9 * [10, 0] + [0, 10] = [9, 0] + [0, 10] = [9, 10]
        // adapt = 0.1 * [9, 10] = [0.9, 1.0]

        assertEquals(expected = 0.9, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 1.0, actual = result[1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD1の_adapt=momentum=0の場合はSGDと同じ`() {
        val momentumD1 = MomentumD1(rate = 0.1, momentum = 0.0)

        // 1回目
        val dw1 = IOType.d1(listOf(1.0, 2.0))
        momentumD1.adapt(dw1)

        // 2回目: momentum=0なので前回の影響を受けない
        val dw2 = IOType.d1(listOf(3.0, 4.0))
        val result = momentumD1.adapt(dw2)
        // v_2 = 0.0 * v_1 + [3, 4] = [3, 4]
        // adapt = 0.1 * [3, 4] = [0.3, 0.4]

        assertEquals(expected = 0.3, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.4, actual = result[1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD2の_adapt=初回呼び出し時はSGDと同じ`() {
        val momentumD2 = MomentumD2(rate = 0.1, momentum = 0.9)

        // dw = [[1, 2], [3, 4]]
        val dw = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }

        // 初回: v_0 = 0, adapt(dw) = 0.1 * dw
        val result = momentumD2.adapt(dw)

        assertEquals(expected = 0.1, actual = result[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.2, actual = result[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.3, actual = result[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.4, actual = result[1, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD2の_adapt=2回目以降はvelocityが蓄積される`() {
        val momentumD2 = MomentumD2(rate = 0.1, momentum = 0.9)

        // 1回目
        val dw1 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        momentumD2.adapt(dw1)
        // v_1 = [[1, 2], [3, 4]]

        // 2回目
        val dw2 = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val result = momentumD2.adapt(dw2)
        // v_2 = 0.9 * [[1, 2], [3, 4]] + [[1, 2], [3, 4]]
        //     = [[0.9, 1.8], [2.7, 3.6]] + [[1, 2], [3, 4]]
        //     = [[1.9, 3.8], [5.7, 7.6]]
        // adapt = 0.1 * v_2 = [[0.19, 0.38], [0.57, 0.76]]

        assertEquals(expected = 0.19, actual = result[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.38, actual = result[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.57, actual = result[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.76, actual = result[1, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD3の_adapt=初回呼び出し時はSGDと同じ`() {
        val momentumD3 = MomentumD3(rate = 0.1, momentum = 0.9)

        // dw = [[[1, 2]]]
        val dw = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }

        // 初回: v_0 = 0, adapt(dw) = 0.1 * dw
        val result = momentumD3.adapt(dw)

        assertEquals(expected = 0.1, actual = result[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.2, actual = result[0, 0, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD3の_adapt=2回目以降はvelocityが蓄積される`() {
        val momentumD3 = MomentumD3(rate = 0.1, momentum = 0.9)

        // 1回目
        val dw1 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }
        momentumD3.adapt(dw1)
        // v_1 = [[[1, 2]]]

        // 2回目
        val dw2 = IOType.d3(1, 1, 2) { _, _, z -> (z + 1).toDouble() }
        val result = momentumD3.adapt(dw2)
        // v_2 = 0.9 * [[[1, 2]]] + [[[1, 2]]]
        //     = [[[0.9, 1.8]]] + [[[1, 2]]]
        //     = [[[1.9, 3.8]]]
        // adapt = 0.1 * v_2 = [[[0.19, 0.38]]]

        assertEquals(expected = 0.19, actual = result[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 0.38, actual = result[0, 0, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD1の_adapt=負の勾配でも正しく動作する`() {
        val momentumD1 = MomentumD1(rate = 0.1, momentum = 0.9)

        // 1回目: dw = [1, 2]
        val dw1 = IOType.d1(listOf(1.0, 2.0))
        momentumD1.adapt(dw1)
        // v_1 = [1, 2]

        // 2回目: dw = [-1, -2]
        val dw2 = IOType.d1(listOf(-1.0, -2.0))
        val result = momentumD1.adapt(dw2)
        // v_2 = 0.9 * [1, 2] + [-1, -2] = [0.9, 1.8] + [-1, -2] = [-0.1, -0.2]
        // adapt = 0.1 * [-0.1, -0.2] = [-0.01, -0.02]

        assertEquals(expected = -0.01, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = -0.02, actual = result[1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `MomentumD1の_adapt=momentum=1の場合は勾配が無限に蓄積される`() {
        val momentumD1 = MomentumD1(rate = 0.1, momentum = 1.0)

        // 1回目: dw = [1]
        val dw1 = IOType.d1(listOf(1.0))
        val result1 = momentumD1.adapt(dw1)
        // v_1 = 1.0 * 0 + 1 = 1
        // adapt = 0.1 * 1 = 0.1
        assertEquals(expected = 0.1, actual = result1[0], absoluteTolerance = 1e-10)

        // 2回目: dw = [1]
        val dw2 = IOType.d1(listOf(1.0))
        val result2 = momentumD1.adapt(dw2)
        // v_2 = 1.0 * 1 + 1 = 2
        // adapt = 0.1 * 2 = 0.2
        assertEquals(expected = 0.2, actual = result2[0], absoluteTolerance = 1e-10)

        // 3回目: dw = [1]
        val dw3 = IOType.d1(listOf(1.0))
        val result3 = momentumD1.adapt(dw3)
        // v_3 = 1.0 * 2 + 1 = 3
        // adapt = 0.1 * 3 = 0.3
        assertEquals(expected = 0.3, actual = result3[0], absoluteTolerance = 1e-10)
    }
}
