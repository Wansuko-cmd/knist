@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.compute.norm.batch

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.assertContentEquals
import com.wsr.knist.network.networkScopeTestRule
import com.wsr.knist.network.process.compute.norm.batch.d1.BatchNormD1
import kotlin.test.Test

class BatchNormD1Test {
    val target get() = BatchNormD1(inputI = 3, e = 1e-6f, momentum = 0.9f)
    val input
        get() = Batch.of(
            *Array(3) { b -> IOType.d1(i = 3) { i -> ((b * 7 + i * 5 + b * i * 2) % 13) / 3f } },
        )

    @Test
    fun `expect=初期状態の統計量で正規化`() = networkScopeTestRule {
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D1>

        assertContentEquals(
            expected = IOType.d1(0.0000f, 1.6667f, 3.3333f),
            actual = actual[0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(2.3333f, 0.3333f, 2.6667f),
            actual = actual[1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.3333f, 3.3333f, 2.0000f),
            actual = actual[2],
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
                    val output = it as Batch<IOType.D1>
                    output * output
                },
            )
        } as Batch<IOType.D1>

        assertContentEquals(
            expected = IOType.d1(0.2976f, -0.7982f, 0.9186f),
            actual = actual[0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0496f, 0.4434f, -1.8371f),
            actual = actual[1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.3472f, 0.3547f, 0.9186f),
            actual = actual[2],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `expect=trainで更新した統計量で正規化`() = networkScopeTestRule {
        val target = target
        with(target) { _train(input = input, env = GraphEnv(), calcDelta = { it }) }
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D1>

        assertContentEquals(
            expected = IOType.d1(-0.0886f, 1.4526f, 3.1806f),
            actual = actual[0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(2.2375f, 0.1518f, 2.4892f),
            actual = actual[1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.2437f, 3.0786f, 1.7977f),
            actual = actual[2],
            absoluteTolerance = 1e-4f,
        )
    }
}
