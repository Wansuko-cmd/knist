@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.reshape.gad

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

class GlobalAverageD3ToD2Test {
    internal fun target(axis: Int) = GlobalAverageD3ToD2(inputI = 2, inputJ = 2, inputK = 2, axis = axis)
    val input
        get() = Batch.of(
            IOType.d3(
                IOType.d2(
                    IOType.d1(1f, 2f),
                    IOType.d1(3f, 4f),
                ),
                IOType.d2(
                    IOType.d1(5f, 6f),
                    IOType.d1(7f, 8f),
                ),
            ),
            IOType.d3(
                IOType.d2(
                    IOType.d1(9f, 10f),
                    IOType.d1(11f, 12f),
                ),
                IOType.d2(
                    IOType.d1(13f, 14f),
                    IOType.d1(15f, 16f),
                ),
            ),
        )

    @Test
    fun `expect axis=0=i方向に平均`() = networkScopeTestRule {
        val actual = with(target(axis = 0)) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D2>

        assertContentEquals(expected = IOType.d1(3f, 4f), actual = actual[0][0])
        assertContentEquals(expected = IOType.d1(5f, 6f), actual = actual[0][1])
        assertContentEquals(expected = IOType.d1(11f, 12f), actual = actual[1][0])
        assertContentEquals(expected = IOType.d1(13f, 14f), actual = actual[1][1])
    }

    @Test
    fun `expect axis=1=j方向に平均`() = networkScopeTestRule {
        val actual = with(target(axis = 1)) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D2>

        assertContentEquals(expected = IOType.d1(2f, 3f), actual = actual[0][0])
        assertContentEquals(expected = IOType.d1(6f, 7f), actual = actual[0][1])
        assertContentEquals(expected = IOType.d1(10f, 11f), actual = actual[1][0])
        assertContentEquals(expected = IOType.d1(14f, 15f), actual = actual[1][1])
    }

    @Test
    fun `expect axis=2=k方向に平均`() = networkScopeTestRule {
        val actual = with(target(axis = 2)) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D2>

        assertContentEquals(expected = IOType.d1(1.5f, 3.5f), actual = actual[0][0])
        assertContentEquals(expected = IOType.d1(5.5f, 7.5f), actual = actual[0][1])
        assertContentEquals(expected = IOType.d1(9.5f, 11.5f), actual = actual[1][0])
        assertContentEquals(expected = IOType.d1(13.5f, 15.5f), actual = actual[1][1])
    }

    @Test
    fun `train axis=0=i方向へ勾配を伝播`() = networkScopeTestRule {
        val actual = with(target(axis = 0)) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D3>

        assertContentEquals(expected = IOType.d1(1.5f, 2f), actual = actual[0][0][0])
        assertContentEquals(expected = IOType.d1(2.5f, 3f), actual = actual[0][0][1])
        assertContentEquals(expected = IOType.d1(1.5f, 2f), actual = actual[0][1][0])
        assertContentEquals(expected = IOType.d1(2.5f, 3f), actual = actual[0][1][1])

        assertContentEquals(expected = IOType.d1(5.5f, 6f), actual = actual[1][0][0])
        assertContentEquals(expected = IOType.d1(6.5f, 7f), actual = actual[1][0][1])
        assertContentEquals(expected = IOType.d1(5.5f, 6f), actual = actual[1][1][0])
        assertContentEquals(expected = IOType.d1(6.5f, 7f), actual = actual[1][1][1])
    }

    @Test
    fun `train axis=1=j方向へ勾配を伝播`() = networkScopeTestRule {
        val actual = with(target(axis = 1)) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D3>

        assertContentEquals(expected = IOType.d1(1f, 1.5f), actual = actual[0][0][0])
        assertContentEquals(expected = IOType.d1(1f, 1.5f), actual = actual[0][0][1])
        assertContentEquals(expected = IOType.d1(3f, 3.5f), actual = actual[0][1][0])
        assertContentEquals(expected = IOType.d1(3f, 3.5f), actual = actual[0][1][1])

        assertContentEquals(expected = IOType.d1(5f, 5.5f), actual = actual[1][0][0])
        assertContentEquals(expected = IOType.d1(5f, 5.5f), actual = actual[1][0][1])
        assertContentEquals(expected = IOType.d1(7f, 7.5f), actual = actual[1][1][0])
        assertContentEquals(expected = IOType.d1(7f, 7.5f), actual = actual[1][1][1])
    }

    @Test
    fun `train axis=2=k方向へ勾配を伝播`() = networkScopeTestRule {
        val actual = with(target(axis = 2)) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D3>

        assertContentEquals(expected = IOType.d1(0.75f, 0.75f), actual = actual[0][0][0])
        assertContentEquals(expected = IOType.d1(1.75f, 1.75f), actual = actual[0][0][1])
        assertContentEquals(expected = IOType.d1(2.75f, 2.75f), actual = actual[0][1][0])
        assertContentEquals(expected = IOType.d1(3.75f, 3.75f), actual = actual[0][1][1])

        assertContentEquals(expected = IOType.d1(4.75f, 4.75f), actual = actual[1][0][0])
        assertContentEquals(expected = IOType.d1(5.75f, 5.75f), actual = actual[1][0][1])
        assertContentEquals(expected = IOType.d1(6.75f, 6.75f), actual = actual[1][1][0])
        assertContentEquals(expected = IOType.d1(7.75f, 7.75f), actual = actual[1][1][1])
    }
}
