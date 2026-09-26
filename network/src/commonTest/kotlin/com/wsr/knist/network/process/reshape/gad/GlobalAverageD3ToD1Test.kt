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
import kotlin.test.assertFails

class GlobalAverageD3ToD1Test {
    internal fun target(axis1: Int, axis2: Int) = GlobalAverageD3ToD1(inputI = 2, inputJ = 2, inputK = 2, axis1 = axis1, axis2 = axis2)
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
    fun `expect axis1=0 axis2=1=iとj方向に平均`() = networkScopeTestRule {
        val actual = with(target(axis1 = 0, axis2 = 1)) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D1>

        assertContentEquals(expected = IOType.d1(4f, 5f), actual = actual[0])
        assertContentEquals(expected = IOType.d1(12f, 13f), actual = actual[1])
    }

    @Test
    fun `expect axis1=0 axis2=2=iとk方向に平均`() = networkScopeTestRule {
        val actual = with(target(axis1 = 0, axis2 = 2)) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D1>

        assertContentEquals(expected = IOType.d1(3.5f, 5.5f), actual = actual[0])
        assertContentEquals(expected = IOType.d1(11.5f, 13.5f), actual = actual[1])
    }

    @Test
    fun `expect axis1=1 axis2=2=jとk方向に平均`() = networkScopeTestRule {
        val actual = with(target(axis1 = 1, axis2 = 2)) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D1>

        assertContentEquals(expected = IOType.d1(2.5f, 6.5f), actual = actual[0])
        assertContentEquals(expected = IOType.d1(10.5f, 14.5f), actual = actual[1])
    }

    @Test
    fun `train axis1=0 axis2=1=iとj方向へ勾配を伝播`() = networkScopeTestRule {
        val actual = with(target(axis1 = 0, axis2 = 1)) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D3>

        assertContentEquals(expected = IOType.d1(1f, 1.25f), actual = actual[0][0][0])
        assertContentEquals(expected = IOType.d1(1f, 1.25f), actual = actual[0][0][1])
        assertContentEquals(expected = IOType.d1(1f, 1.25f), actual = actual[0][1][0])
        assertContentEquals(expected = IOType.d1(1f, 1.25f), actual = actual[0][1][1])

        assertContentEquals(expected = IOType.d1(3f, 3.25f), actual = actual[1][0][0])
        assertContentEquals(expected = IOType.d1(3f, 3.25f), actual = actual[1][0][1])
        assertContentEquals(expected = IOType.d1(3f, 3.25f), actual = actual[1][1][0])
        assertContentEquals(expected = IOType.d1(3f, 3.25f), actual = actual[1][1][1])
    }

    @Test
    fun `train axis1=0 axis2=2=iとk方向へ勾配を伝播`() = networkScopeTestRule {
        val actual = with(target(axis1 = 0, axis2 = 2)) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D3>

        assertContentEquals(expected = IOType.d1(0.875f, 0.875f), actual = actual[0][0][0])
        assertContentEquals(expected = IOType.d1(1.375f, 1.375f), actual = actual[0][0][1])
        assertContentEquals(expected = IOType.d1(0.875f, 0.875f), actual = actual[0][1][0])
        assertContentEquals(expected = IOType.d1(1.375f, 1.375f), actual = actual[0][1][1])

        assertContentEquals(expected = IOType.d1(2.875f, 2.875f), actual = actual[1][0][0])
        assertContentEquals(expected = IOType.d1(3.375f, 3.375f), actual = actual[1][0][1])
        assertContentEquals(expected = IOType.d1(2.875f, 2.875f), actual = actual[1][1][0])
        assertContentEquals(expected = IOType.d1(3.375f, 3.375f), actual = actual[1][1][1])
    }

    @Test
    fun `train axis1=1 axis2=2=jとk方向へ勾配を伝播`() = networkScopeTestRule {
        val actual = with(target(axis1 = 1, axis2 = 2)) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D3>

        assertContentEquals(expected = IOType.d1(0.625f, 0.625f), actual = actual[0][0][0])
        assertContentEquals(expected = IOType.d1(0.625f, 0.625f), actual = actual[0][0][1])
        assertContentEquals(expected = IOType.d1(1.625f, 1.625f), actual = actual[0][1][0])
        assertContentEquals(expected = IOType.d1(1.625f, 1.625f), actual = actual[0][1][1])

        assertContentEquals(expected = IOType.d1(2.625f, 2.625f), actual = actual[1][0][0])
        assertContentEquals(expected = IOType.d1(2.625f, 2.625f), actual = actual[1][0][1])
        assertContentEquals(expected = IOType.d1(3.625f, 3.625f), actual = actual[1][1][0])
        assertContentEquals(expected = IOType.d1(3.625f, 3.625f), actual = actual[1][1][1])
    }

    @Test
    fun `axis1とaxis2が不正な場合は例外を投げる`() = networkScopeTestRule {
        assertFails { target(axis1 = 1, axis2 = 0) }
        assertFails { target(axis1 = 0, axis2 = 0) }
        assertFails { target(axis1 = 0, axis2 = 3) }
    }
}
