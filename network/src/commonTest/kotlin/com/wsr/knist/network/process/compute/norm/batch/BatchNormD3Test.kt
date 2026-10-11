@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.compute.norm.batch

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.d3
import com.wsr.knist.core.get
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.assertContentEquals
import com.wsr.knist.network.networkScopeTestRule
import com.wsr.knist.network.process.compute.norm.batch.d3.BatchNormD3
import kotlin.test.Test

class BatchNormD3Test {
    val target get() = BatchNormD3(inputI = 2, inputJ = 2, inputK = 3, e = 1e-6f, momentum = 0.9f)
    val input
        get() = Batch.of(
            *Array(3) { b -> IOType.d3(i = 2, j = 2, k = 3) { i, j, k -> ((b * 7 + i * 5 + j * 3 + k * 11 + i * j * k) % 13) / 3f } },
        )

    @Test
    fun `expect=初期状態の統計量で正規化`() = networkScopeTestRule {
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D3>

        assertContentEquals(
            expected = IOType.d1(0.0000f, 3.6667f, 3.0000f),
            actual = actual[0][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(1.0000f, 0.3333f, 4.0000f),
            actual = actual[0][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(1.6667f, 1.0000f, 0.3333f),
            actual = actual[0][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(2.6667f, 2.3333f, 2.0000f),
            actual = actual[0][1][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(2.3333f, 1.6667f, 1.0000f),
            actual = actual[1][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(3.3333f, 2.6667f, 2.0000f),
            actual = actual[1][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(4.0000f, 3.3333f, 2.6667f),
            actual = actual[1][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.6667f, 0.3333f, 0.0000f),
            actual = actual[1][1][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.3333f, 4.0000f, 3.3333f),
            actual = actual[2][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(1.3333f, 0.6667f, 0.0000f),
            actual = actual[2][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(2.0000f, 1.3333f, 0.6667f),
            actual = actual[2][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(3.0000f, 2.6667f, 2.3333f),
            actual = actual[2][1][1],
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
                    val output = it as Batch<IOType.D3>
                    output * output
                },
            )
        } as Batch<IOType.D3>

        assertContentEquals(
            expected = IOType.d1(0.2976f, -0.3472f, -0.3472f),
            actual = actual[0][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.2976f, 0.2976f, 0.3062f),
            actual = actual[0][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.2976f, 0.2976f, 0.2976f),
            actual = actual[0][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.3472f, -0.3472f, -0.3472f),
            actual = actual[0][1][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0496f, 0.0496f, 0.0496f),
            actual = actual[1][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0496f, 0.0496f, -0.6124f),
            actual = actual[1][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0496f, 0.0496f, 0.0496f),
            actual = actual[1][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0496f, 0.0496f, 0.0496f),
            actual = actual[1][1][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.3472f, 0.2976f, 0.2976f),
            actual = actual[2][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.3472f, -0.3472f, 0.3062f),
            actual = actual[2][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.3472f, -0.3472f, -0.3472f),
            actual = actual[2][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.2976f, 0.2976f, 0.2976f),
            actual = actual[2][1][1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `expect=trainで更新した統計量で正規化`() = networkScopeTestRule {
        val target = target
        with(target) { _train(input = input, env = GraphEnv(), calcDelta = { it }) }
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D3>

        assertContentEquals(
            expected = IOType.d1(-0.0886f, 3.3452f, 2.7471f),
            actual = actual[0][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.8086f, 0.2105f, 3.5181f),
            actual = actual[0][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(1.4068f, 0.8086f, 0.2105f),
            actual = actual[0][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(2.4480f, 2.1489f, 1.8499f),
            actual = actual[0][1][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(2.2375f, 1.3514f, 0.7532f),
            actual = actual[1][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(3.1348f, 2.5366f, 1.6665f),
            actual = actual[1][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(3.7329f, 3.1348f, 2.5366f),
            actual = actual[1][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.4542f, 0.1551f, -0.1440f),
            actual = actual[1][1][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.2437f, 3.6776f, 3.0794f),
            actual = actual[2][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(1.1409f, 0.5428f, -0.1852f),
            actual = actual[2][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(1.7391f, 1.1409f, 0.5428f),
            actual = actual[2][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(2.7803f, 2.4812f, 2.1822f),
            actual = actual[2][1][1],
            absoluteTolerance = 1e-4f,
        )
    }
}
