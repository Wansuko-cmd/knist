@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.compute.norm.minmax

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.assertContentEquals
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test

class MinMaxNormD1Test {
    val target get() = MinMaxNormD1(inputI = 3, e = 1e-6f)
    val input
        get() = Batch.of(
            IOType.d1(3) { it.toFloat() },
            IOType.d1(3) { it * 2f },
        )

    @Test
    fun `expect=最小値0最大値1へ正規化`() = networkScopeTestRule {
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D1>

        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.5000f, 1.0000f),
            actual = actual[0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.5000f, 1.0000f),
            actual = actual[1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `train=正規化および勾配を伝播`() = networkScopeTestRule {
        val actual = with(target) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D1>

        assertContentEquals(
            expected = IOType.d1(-0.1250f, 0.2500f, -0.1250f),
            actual = actual[0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.0625f, 0.1250f, -0.0625f),
            actual = actual[1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `expect=定数入力でもNaNにならず0を返す`() = networkScopeTestRule {
        val constant = Batch.of(IOType.d1(2f, 2f, 2f))

        val actual = with(target) { _expect(input = constant, env = GraphEnv()) } as Batch<IOType.D1>

        assertContentEquals(
            expected = IOType.d1(0f, 0f, 0f),
            actual = actual[0],
            absoluteTolerance = 1e-4f,
        )
    }
}
