@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.reshape.gad

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.get
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.assertContentEquals
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test

class GlobalAverageD2ToD1Test {
    internal fun target(axis: Int) = GlobalAverageD2ToD1(inputI = 2, inputJ = 3, axis = axis)
    val input
        get() = Batch.of(
            IOType.d2(
                IOType.d1(1f, 2f, 3f),
                IOType.d1(4f, 5f, 6f),
            ),
            IOType.d2(
                IOType.d1(7f, 8f, 9f),
                IOType.d1(10f, 11f, 12f),
            ),
        )

    @Test
    fun `expect axis=1=行方向に平均`() = networkScopeTestRule {
        val actual = with(target(axis = 1)) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D1>

        assertContentEquals(expected = IOType.d1(2f, 5f), actual = actual[0])
        assertContentEquals(expected = IOType.d1(8f, 11f), actual = actual[1])
    }

    @Test
    fun `expect axis=0=列方向に平均`() = networkScopeTestRule {
        val actual = with(target(axis = 0)) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D1>

        assertContentEquals(expected = IOType.d1(2.5f, 3.5f, 4.5f), actual = actual[0])
        assertContentEquals(expected = IOType.d1(8.5f, 9.5f, 10.5f), actual = actual[1])
    }

    @Test
    fun `train axis=1=行方向へ勾配を伝播`() = networkScopeTestRule {
        val actual = with(target(axis = 1)) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D2>

        assertContentEquals(expected = IOType.d1(0.6667f, 0.6667f, 0.6667f), actual = actual[0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(1.6667f, 1.6667f, 1.6667f), actual = actual[0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(2.6667f, 2.6667f, 2.6667f), actual = actual[1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(3.6667f, 3.6667f, 3.6667f), actual = actual[1][1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `train axis=0=列方向へ勾配を伝播`() = networkScopeTestRule {
        val actual = with(target(axis = 0)) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D2>

        assertContentEquals(expected = IOType.d1(1.25f, 1.75f, 2.25f), actual = actual[0][0])
        assertContentEquals(expected = IOType.d1(1.25f, 1.75f, 2.25f), actual = actual[0][1])
        assertContentEquals(expected = IOType.d1(4.25f, 4.75f, 5.25f), actual = actual[1][0])
        assertContentEquals(expected = IOType.d1(4.25f, 4.75f, 5.25f), actual = actual[1][1])
    }
}
