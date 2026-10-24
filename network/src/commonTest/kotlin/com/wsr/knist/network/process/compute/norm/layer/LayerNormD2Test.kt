@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.compute.norm.layer

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.get
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.assertContentEquals
import com.wsr.knist.network.networkScopeTestRule
import com.wsr.knist.network.process.compute.norm.layer.d2.LayerNormD2
import kotlin.test.Test

class LayerNormD2Test {
    val target get() = LayerNormD2(inputI = 2, inputJ = 3, e = 1e-6f)
    val input
        get() = Batch.of(
            IOType.d2(
                IOType.d1(3) { it.toFloat() },
                IOType.d1(3) { it * 2f },
            ),
            IOType.d2(
                IOType.d1(3) { 10f % (it + 1) },
                IOType.d1(3) { it * 0.3f },
            ),
        )

    @Test
    fun `expect=層正規化`() = networkScopeTestRule {
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D2>

        assertContentEquals(
            expected = IOType.d1(-1.0834f, -0.3611f, 0.3611f),
            actual = actual[0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-1.0834f, 0.3611f, 1.8057f),
            actual = actual[0][1],
            absoluteTolerance = 1e-4f,
        )

        assertContentEquals(
            expected = IOType.d1(-0.8421f, -0.8421f, 1.8172f),
            actual = actual[1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.8421f, -0.0443f, 0.7535f),
            actual = actual[1][1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `train=正規化および勾配を伝播`() = networkScopeTestRule {
        val delta = Batch.of(
            IOType.d2(2, 3) { i, j -> ((i * 3 + j) * (i * 3 + j) % 5).toFloat() },
            IOType.d2(2, 3) { i, j -> (((i * 3 + j) * (i * 3 + j) + 2) % 5).toFloat() },
        )
        val actual = with(target) {
            _train(
                input = input,
                env = GraphEnv(),
                calcDelta = { delta },
            )
        } as Batch<IOType.D2>

        assertContentEquals(
            expected = IOType.d1(-1.5807f, -0.6072f, 1.8110f),
            actual = actual[0][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(1.3085f, -0.3559f, -0.5758f),
            actual = actual[0][1],
            absoluteTolerance = 1e-3f,
        )

        assertContentEquals(
            expected = IOType.d1(-0.6949f, 1.9645f, -1.1599f),
            actual = actual[1][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(-3.3543f, 2.6229f, 0.6218f),
            actual = actual[1][1],
            absoluteTolerance = 1e-3f,
        )
    }
}
