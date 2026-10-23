@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.compute.norm.layer

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.d3
import com.wsr.knist.core.get
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.assertContentEquals
import com.wsr.knist.network.networkScopeTestRule
import com.wsr.knist.network.process.compute.norm.layer.d3.LayerNormD3
import kotlin.test.Test

class LayerNormD3Test {
    val target get() = LayerNormD3(inputI = 2, inputJ = 2, inputK = 3, e = 1e-6f)
    val input
        get() = Batch.of(
            IOType.d3(
                IOType.d2(
                    IOType.d1(3) { it.toFloat() },
                    IOType.d1(3) { it * 2f },
                ),
                IOType.d2(
                    IOType.d1(3) { 10f % (it + 1) },
                    IOType.d1(3) { it * 0.3f },
                ),
            ),
            IOType.d3(
                IOType.d2(
                    IOType.d1(3) { it * it * 2f },
                    IOType.d1(3) { it + 5f },
                ),
                IOType.d2(
                    IOType.d1(3) { it % 1.5f },
                    IOType.d1(3) { 10f / (it - 5) },
                ),
            ),
        )

    @Test
    fun `expect=層正規化`() = networkScopeTestRule {
        val actual = with(target) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D3>

        assertContentEquals(
            expected = IOType.d1(-0.7734f, 0.0780f, 0.9295f),
            actual = actual[0][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.7734f, 0.9295f, 2.6326f),
            actual = actual[0][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.7734f, -0.7734f, 0.0780f),
            actual = actual[0][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.7734f, -0.5180f, -0.2625f),
            actual = actual[0][1][1],
            absoluteTolerance = 1e-4f,
        )

        assertContentEquals(
            expected = IOType.d1(-0.4921f, 0.0530f, 1.6885f),
            actual = actual[1][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.8707f, 1.1433f, 1.4159f),
            actual = actual[1][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.4921f, -0.2195f, -0.3558f),
            actual = actual[1][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-1.0373f, -1.1736f, -1.4008f),
            actual = actual[1][1][1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `train=正規化および勾配を伝播`() = networkScopeTestRule {
        val delta = Batch.of(
            IOType.d3(2, 2, 3) { i, j, k -> (i * 6 + j * 3 + k).toFloat() },
            IOType.d3(2, 2, 3) { i, j, k -> ((i * 6 + j * 3 + k) % 4).toFloat() },
        )
        val actual = with(target) {
            _train(
                input = input,
                env = GraphEnv(),
                calcDelta = { delta },
            )
        } as Batch<IOType.D3>

        assertContentEquals(
            expected = IOType.d1(-5.1204f, -3.7878f, -2.4552f),
            actual = actual[0][0][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(-2.5658f, -0.7521f, 1.0616f),
            actual = actual[0][0][1],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.0112f, 0.8403f, 2.1729f),
            actual = actual[0][1][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(2.5434f, 3.5392f, 4.5351f),
            actual = actual[0][1][1],
            absoluteTolerance = 1e-3f,
        )

        assertContentEquals(
            expected = IOType.d1(-0.4287f, -0.1342f, 0.2043f),
            actual = actual[1][0][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(0.4439f, -0.3629f, -0.0793f),
            actual = actual[1][0][1],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(0.1165f, 0.4001f, -0.4232f),
            actual = actual[1][1][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.1781f, 0.0891f, 0.3525f),
            actual = actual[1][1][1],
            absoluteTolerance = 1e-3f,
        )
    }
}
