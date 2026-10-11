@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.compute.norm.batch

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.get
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.assertContentEquals
import com.wsr.knist.network.networkScopeTestRule
import com.wsr.knist.network.process.compute.norm.batch.d2.BatchNormD2
import kotlin.test.Test

class BatchNormD2Test {
    val target get() = BatchNormD2(inputI = 2, inputJ = 3, e = 1e-6f, momentum = 0.9f)
    val input
        get() = Batch.of(
            *Array(3) { b -> IOType.d2(i = 2, j = 3) { i, j -> ((b * 7 + i * 5 + j * 3 + i * j * 2) % 13) / 3f } },
        )

    @Test
    fun `expect=初期状態の統計量で正規化`() = networkScopeTestRule {
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D2>

        assertContentEquals(
            expected = IOType.d1(0.0000f, 1.0000f, 2.0000f),
            actual = actual[0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(1.6667f, 3.3333f, 0.6667f),
            actual = actual[0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(2.3333f, 3.3333f, 0.0000f),
            actual = actual[1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(4.0000f, 1.3333f, 3.0000f),
            actual = actual[1][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.3333f, 1.3333f, 2.3333f),
            actual = actual[2][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(2.0000f, 3.6667f, 1.0000f),
            actual = actual[2][1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `train=batch方向で正規化および勾配を伝播`() = networkScopeTestRule {
        val actual = with(target) {
            _train(
                input = input,
                env = GraphEnv(),
                calcDelta = {
                    val output = it as Batch<IOType.D2>
                    output * output
                },
            )
        } as Batch<IOType.D2>

        assertContentEquals(
            expected = IOType.d1(0.2976f, 0.2976f, -0.3472f),
            actual = actual[0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.2976f, -0.3472f, 0.2976f),
            actual = actual[0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0496f, 0.0496f, 0.0496f),
            actual = actual[1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0496f, 0.0496f, 0.0496f),
            actual = actual[1][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.3472f, -0.3472f, 0.2976f),
            actual = actual[2][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.3472f, 0.2976f, -0.3472f),
            actual = actual[2][1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `expect=trainで更新した統計量で正規化`() = networkScopeTestRule {
        val target = target
        with(target) { _train(input = input, env = GraphEnv(), calcDelta = { it }) }
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D2>

        assertContentEquals(
            expected = IOType.d1(-0.0886f, 0.8086f, 1.8499f),
            actual = actual[0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(1.4068f, 3.0462f, 0.5095f),
            actual = actual[0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(2.2375f, 3.1348f, -0.1440f),
            actual = actual[1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(3.7329f, 1.0523f, 2.8357f),
            actual = actual[1][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.2437f, 1.1409f, 2.1822f),
            actual = actual[2][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(1.7391f, 3.3785f, 0.8418f),
            actual = actual[2][1],
            absoluteTolerance = 1e-4f,
        )
    }
}
