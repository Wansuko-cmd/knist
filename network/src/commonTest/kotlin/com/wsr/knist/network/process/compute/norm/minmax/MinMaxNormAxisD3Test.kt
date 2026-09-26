@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.compute.norm.minmax

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.d3
import com.wsr.knist.core.get
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.assertContentEquals
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test
import kotlin.test.assertFails

class MinMaxNormAxisD3Test {
    val target0 get() = MinMaxNormAxisD3(inputI = 3, inputJ = 2, inputK = 2, axis = 0)
    val input0
        get() = Batch.of(
            IOType.d3(
                IOType.d2(IOType.d1(1f, 2f), IOType.d1(3f, 4f)),
                IOType.d2(IOType.d1(5f, 6f), IOType.d1(7f, 8f)),
                IOType.d2(IOType.d1(9f, 0f), IOType.d1(2f, 10f)),
            ),
            IOType.d3(
                IOType.d2(IOType.d1(2f, 1f), IOType.d1(4f, 3f)),
                IOType.d2(IOType.d1(6f, 5f), IOType.d1(8f, 7f)),
                IOType.d2(IOType.d1(0f, 9f), IOType.d1(10f, 2f)),
            ),
        )

    val target1 get() = MinMaxNormAxisD3(inputI = 2, inputJ = 3, inputK = 2, axis = 1)
    val input1
        get() = Batch.of(
            IOType.d3(
                IOType.d2(IOType.d1(1f, 2f), IOType.d1(3f, 4f), IOType.d1(9f, 0f)),
                IOType.d2(IOType.d1(5f, 6f), IOType.d1(7f, 8f), IOType.d1(2f, 10f)),
            ),
            IOType.d3(
                IOType.d2(IOType.d1(2f, 1f), IOType.d1(4f, 3f), IOType.d1(0f, 9f)),
                IOType.d2(IOType.d1(6f, 5f), IOType.d1(8f, 7f), IOType.d1(10f, 2f)),
            ),
        )

    val target2 get() = MinMaxNormAxisD3(inputI = 2, inputJ = 2, inputK = 3, axis = 2)
    val input2
        get() = Batch.of(
            IOType.d3(
                IOType.d2(IOType.d1(1f, 2f, 9f), IOType.d1(3f, 4f, 0f)),
                IOType.d2(IOType.d1(5f, 6f, 2f), IOType.d1(7f, 8f, 10f)),
            ),
            IOType.d3(
                IOType.d2(IOType.d1(2f, 1f, 0f), IOType.d1(4f, 3f, 9f)),
                IOType.d2(IOType.d1(6f, 5f, 10f), IOType.d1(8f, 7f, 2f)),
            ),
        )

    @Test
    fun `Axis0_expect=i方向にmin-max正規化`() = networkScopeTestRule {
        val actual = with(target0) { _expect(input = input0, env = GraphEnv()) } as Batch<IOType.D3>

        assertContentEquals(expected = IOType.d1(0f, 0.3333f), actual = actual[0][0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.2f, 0f), actual = actual[0][0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.5f, 1f), actual = actual[0][1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(1f, 0.6667f), actual = actual[0][1][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(1f, 0f), actual = actual[0][2][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0f, 1f), actual = actual[0][2][1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis0_train=i方向へ勾配を伝播`() = networkScopeTestRule {
        val actual = with(target0) {
            _train(input = input0, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D3>

        assertContentEquals(expected = IOType.d1(-0.03125f, 0.05556f), actual = actual[0][0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.04f, -0.03704f), actual = actual[0][0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.0625f, -0.01852f), actual = actual[0][1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.008f, 0.11111f), actual = actual[0][1][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.03125f, -0.03704f), actual = actual[0][2][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.032f, -0.07407f), actual = actual[0][2][1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis1_expect=j方向にmin-max正規化`() = networkScopeTestRule {
        val actual = with(target1) { _expect(input = input1, env = GraphEnv()) } as Batch<IOType.D3>

        assertContentEquals(expected = IOType.d1(0f, 0.5f), actual = actual[0][0][0])
        assertContentEquals(expected = IOType.d1(0.25f, 1f), actual = actual[0][0][1])
        assertContentEquals(expected = IOType.d1(1f, 0f), actual = actual[0][0][2])
        assertContentEquals(expected = IOType.d1(0.6f, 0f), actual = actual[0][1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(1f, 0.5f), actual = actual[0][1][1])
        assertContentEquals(expected = IOType.d1(0f, 1f), actual = actual[0][1][2])
    }

    @Test
    fun `Axis1_train=j方向へ勾配を伝播`() = networkScopeTestRule {
        val actual = with(target1) {
            _train(input = input1, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D3>

        assertContentEquals(expected = IOType.d1(-0.02344f, 0.125f), actual = actual[0][0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.03125f, -0.0625f), actual = actual[0][0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.00781f, -0.0625f), actual = actual[0][0][2], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.12f, -0.0625f), actual = actual[0][1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.072f, 0.125f), actual = actual[0][1][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.048f, -0.0625f), actual = actual[0][1][2], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis2_expect=k方向にmin-max正規化`() = networkScopeTestRule {
        val actual = with(target2) { _expect(input = input2, env = GraphEnv()) } as Batch<IOType.D3>

        assertContentEquals(expected = IOType.d1(0f, 0.125f, 1f), actual = actual[0][0][0])
        assertContentEquals(expected = IOType.d1(0.75f, 1f, 0f), actual = actual[0][0][1])
        assertContentEquals(expected = IOType.d1(0.75f, 1f, 0f), actual = actual[0][1][0])
        assertContentEquals(expected = IOType.d1(0f, 0.3333f, 1f), actual = actual[0][1][1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis2_train=k方向へ勾配を伝播`() = networkScopeTestRule {
        val actual = with(target2) {
            _train(input = input2, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D3>

        assertContentEquals(expected = IOType.d1(-0.01367f, 0.01563f, -0.00195f), actual = actual[0][0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.1875f, -0.1406f, -0.0469f), actual = actual[0][0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.1875f, -0.1406f, -0.0469f), actual = actual[0][1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.07407f, 0.11111f, -0.03704f), actual = actual[0][1][1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `axisが不正な場合は例外を投げる`() {
        assertFails { MinMaxNormAxisD3(inputI = 2, inputJ = 2, inputK = 2, axis = 3) }
        assertFails { MinMaxNormAxisD3(inputI = 2, inputJ = 2, inputK = 2, axis = -1) }
    }
}
