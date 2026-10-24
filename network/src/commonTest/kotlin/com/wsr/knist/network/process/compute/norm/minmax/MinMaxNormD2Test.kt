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

class MinMaxNormD2Test {
    val target get() = MinMaxNormD2(inputI = 2, inputJ = 3, e = 1e-6f)
    val input
        get() = Batch.of(
            IOType.d2(
                IOType.d1(1f, 3f, 5f),
                IOType.d1(2f, 4f, 6f),
            ),
            IOType.d2(
                IOType.d1(7f, 2f, 9f),
                IOType.d1(4f, 1f, 6f),
            ),
        )

    @Test
    fun `expect=最小値0最大値1へ正規化`() = networkScopeTestRule {
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D2>

        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.4000f, 0.8000f),
            actual = actual[0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.2000f, 0.6000f, 1.0000f),
            actual = actual[0][1],
            absoluteTolerance = 1e-4f,
        )

        assertContentEquals(
            expected = IOType.d1(0.7500f, 0.1250f, 1.0000f),
            actual = actual[1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.3750f, 0.0000f, 0.6250f),
            actual = actual[1][1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `train=正規化および勾配を伝播`() = networkScopeTestRule {
        val actual = with(target) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D2>

        assertContentEquals(
            expected = IOType.d1(-0.1600f, 0.0800f, 0.1600f),
            actual = actual[0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0400f, 0.1200f, -0.2400f),
            actual = actual[0][1],
            absoluteTolerance = 1e-4f,
        )

        assertContentEquals(
            expected = IOType.d1(0.0937f, 0.0156f, -0.1387f),
            actual = actual[1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0469f, -0.0957f, 0.0781f),
            actual = actual[1][1],
            absoluteTolerance = 1e-4f,
        )
    }
}
