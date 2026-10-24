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

class MinMaxNormD3Test {
    val target get() = MinMaxNormD3(inputI = 2, inputJ = 2, inputK = 2, e = 1e-6f)
    val input
        get() = Batch.of(
            IOType.d3(
                IOType.d2(
                    IOType.d1(1f, 3f),
                    IOType.d1(5f, 7f),
                ),
                IOType.d2(
                    IOType.d1(2f, 9f),
                    IOType.d1(4f, 6f),
                ),
            ),
            IOType.d3(
                IOType.d2(
                    IOType.d1(8f, 2f),
                    IOType.d1(5f, 1f),
                ),
                IOType.d2(
                    IOType.d1(6f, 3f),
                    IOType.d1(9f, 4f),
                ),
            ),
        )

    @Test
    fun `expect=最小値0最大値1へ正規化`() = networkScopeTestRule {
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D3>

        assertContentEquals(expected = IOType.d1(0.0000f, 0.2500f), actual = actual[0][0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.5000f, 0.7500f), actual = actual[0][0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.1250f, 1.0000f), actual = actual[0][1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.3750f, 0.6250f), actual = actual[0][1][1], absoluteTolerance = 1e-4f)

        assertContentEquals(expected = IOType.d1(0.8750f, 0.1250f), actual = actual[1][0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.5000f, 0.0000f), actual = actual[1][0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.6250f, 0.2500f), actual = actual[1][1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(1.0000f, 0.3750f), actual = actual[1][1][1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `train=正規化および勾配を伝播`() = networkScopeTestRule {
        val actual = with(target) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D3>

        assertContentEquals(
            expected = IOType.d1(-0.1504f, 0.0313f),
            actual = actual[0][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(expected = IOType.d1(0.0625f, 0.0937f), actual = actual[0][0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(
            expected = IOType.d1(0.0156f, -0.1777f),
            actual = actual[0][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(expected = IOType.d1(0.0469f, 0.0781f), actual = actual[0][1][1], absoluteTolerance = 1e-4f)

        assertContentEquals(expected = IOType.d1(0.1094f, 0.0156f), actual = actual[1][0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(
            expected = IOType.d1(0.0625f, -0.1406f),
            actual = actual[1][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(expected = IOType.d1(0.0781f, 0.0313f), actual = actual[1][1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(
            expected = IOType.d1(-0.2031f, 0.0469f),
            actual = actual[1][1][1],
            absoluteTolerance = 1e-4f,
        )
    }
}
