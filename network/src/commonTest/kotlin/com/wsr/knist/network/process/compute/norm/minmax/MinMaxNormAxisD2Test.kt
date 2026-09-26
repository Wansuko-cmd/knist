@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.compute.norm.minmax

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.get
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.assertContentEquals
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test
import kotlin.test.assertFails

class MinMaxNormAxisD2Test {
    val target1 get() = MinMaxNormAxisD2(inputI = 2, inputJ = 3, axis = 1)
    val input1
        get() = Batch.of(
            IOType.d2(IOType.d1(1f, 5f, 3f), IOType.d1(4f, 2f, 6f)),
            IOType.d2(IOType.d1(8f, 2f, 5f), IOType.d1(9f, 1f, 4f)),
        )

    val target0 get() = MinMaxNormAxisD2(inputI = 3, inputJ = 3, axis = 0)
    val input0
        get() = Batch.of(
            IOType.d2(IOType.d1(1f, 5f, 3f), IOType.d1(4f, 2f, 8f), IOType.d1(6f, 0f, 7f)),
            IOType.d2(IOType.d1(2f, 9f, 4f), IOType.d1(7f, 1f, 3f), IOType.d1(5f, 8f, 0f)),
        )

    @Test
    fun `Axis1_expect=行方向にmin-max正規化`() = networkScopeTestRule {
        val actual = with(target1) { _expect(input = input1, env = GraphEnv()) } as Batch<IOType.D2>

        assertContentEquals(expected = IOType.d1(0f, 1f, 0.5f), actual = actual[0][0])
        assertContentEquals(expected = IOType.d1(0.5f, 0f, 1f), actual = actual[0][1])
        assertContentEquals(expected = IOType.d1(1f, 0f, 0.5f), actual = actual[1][0])
        assertContentEquals(expected = IOType.d1(1f, 0f, 0.375f), actual = actual[1][1])
    }

    @Test
    fun `Axis1_train=行方向へ勾配を伝播`() = networkScopeTestRule {
        val actual = with(target1) {
            _train(input = input1, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D2>

        assertContentEquals(expected = IOType.d1(-0.0625f, -0.0625f, 0.125f), actual = actual[0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.125f, -0.0625f, -0.0625f), actual = actual[0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.041667f, -0.041667f, 0.083333f), actual = actual[1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.017578f, -0.029297f, 0.046875f), actual = actual[1][1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis0_expect=列方向にmin-max正規化`() = networkScopeTestRule {
        val actual = with(target0) { _expect(input = input0, env = GraphEnv()) } as Batch<IOType.D2>

        assertContentEquals(expected = IOType.d1(0f, 1f, 0f), actual = actual[0][0])
        assertContentEquals(expected = IOType.d1(0.6f, 0.4f, 1f), actual = actual[0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(1f, 0f, 0.8f), actual = actual[0][2], absoluteTolerance = 1e-4f)

        assertContentEquals(expected = IOType.d1(0f, 1f, 1f), actual = actual[1][0])
        assertContentEquals(expected = IOType.d1(1f, 0f, 0.75f), actual = actual[1][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.6f, 0.875f, 0f), actual = actual[1][2], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis0_train=列方向へ勾配を伝播`() = networkScopeTestRule {
        val actual = with(target0) {
            _train(input = input0, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D2>

        assertContentEquals(expected = IOType.d1(-0.048f, -0.032f, -0.032f), actual = actual[0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.12f, 0.08f, -0.128f), actual = actual[0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.072f, -0.048f, 0.16f), actual = actual[0][2], absoluteTolerance = 1e-4f)

        assertContentEquals(expected = IOType.d1(-0.048f, -0.0957f, -0.1406f), actual = actual[1][0], absoluteTolerance = 1e-3f)
        assertContentEquals(expected = IOType.d1(-0.072f, -0.0137f, 0.1875f), actual = actual[1][1], absoluteTolerance = 1e-3f)
        assertContentEquals(expected = IOType.d1(0.12f, 0.1094f, -0.0469f), actual = actual[1][2], absoluteTolerance = 1e-3f)
    }

    @Test
    fun `axisが不正な場合は例外を投げる`() {
        assertFails { MinMaxNormAxisD2(inputI = 2, inputJ = 2, axis = 2) }
        assertFails { MinMaxNormAxisD2(inputI = 2, inputJ = 2, axis = -1) }
    }
}
