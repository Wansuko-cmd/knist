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
import com.wsr.knist.network.process.compute.norm.layer.d3.LayerNormAxisD3
import kotlin.test.Test

class LayerNormAxisD3Test {
    val target0 get() = LayerNormAxisD3(inputI = 2, inputJ = 2, inputK = 3, axis = 0, e = 1e-6f)
    val target1 get() = LayerNormAxisD3(inputI = 2, inputJ = 2, inputK = 3, axis = 1, e = 1e-6f)
    val target2 get() = LayerNormAxisD3(inputI = 2, inputJ = 2, inputK = 3, axis = 2, e = 1e-6f)
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
    fun `Axis0_expect=axis0で層正規化`() = networkScopeTestRule {
        val actual = with(target0) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D3>

        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.9999f, 0.9999f),
            actual = actual[0][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.9999f, 0.9999f),
            actual = actual[0][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, -0.9999f, -0.9999f),
            actual = actual[0][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, -0.9999f, -0.9999f),
            actual = actual[0][1][1],
            absoluteTolerance = 1e-4f,
        )

        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.9999f, 0.9999f),
            actual = actual[1][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.9999f, 1.0000f, 1.0000f),
            actual = actual[1][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, -0.9999f, -0.9999f),
            actual = actual[1][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.9999f, -1.0000f, -1.0000f),
            actual = actual[1][1][1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `Axis0_train=axis0で正規化および勾配を伝播`() = networkScopeTestRule {
        val delta = Batch.of(
            IOType.d3(2, 2, 3) { i, j, k -> ((i * 6 + j * 3 + k) * (i * 6 + j * 3 + k) % 5).toFloat() },
            IOType.d3(2, 2, 3) { i, j, k -> (((i * 6 + j * 3 + k) * (i * 6 + j * 3 + k) + 2) % 5).toFloat() },
        )
        val actual = with(target0) {
            _train(
                input = input,
                env = GraphEnv(),
                calcDelta = { delta },
            )
        } as Batch<IOType.D3>

        assertContentEquals(
            expected = IOType.d1(-500.0000f, 0.0000f, 0.0000f),
            actual = actual[0][0][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(1500.0000f, 0.0000f, 0.0000f),
            actual = actual[0][0][1],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(500.0000f, 0.0000f, 0.0000f),
            actual = actual[0][1][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(-1500.0000f, 0.0000f, 0.0000f),
            actual = actual[0][1][1],
            absoluteTolerance = 1e-3f,
        )

        assertContentEquals(
            expected = IOType.d1(-500.0000f, 0.0000f, 0.0000f),
            actual = actual[1][0][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.0000f, 0.0000f),
            actual = actual[1][0][1],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(500.0000f, 0.0000f, 0.0000f),
            actual = actual[1][1][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.0000f, 0.0000f),
            actual = actual[1][1][1],
            absoluteTolerance = 1e-3f,
        )
    }

    @Test
    fun `Axis1_expect=axis1で層正規化`() = networkScopeTestRule {
        val actual = with(target1) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D3>

        assertContentEquals(
            expected = IOType.d1(0.0000f, -0.9999f, -0.9999f),
            actual = actual[0][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.9999f, 0.9999f),
            actual = actual[0][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, -0.9999f, 0.9999f),
            actual = actual[0][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.9999f, -0.9999f),
            actual = actual[0][1][1],
            absoluteTolerance = 1e-4f,
        )

        assertContentEquals(
            expected = IOType.d1(-0.9999f, -0.9999f, 0.9999f),
            actual = actual[1][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.9999f, 0.9999f, -0.9999f),
            actual = actual[1][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(0.9999f, 0.9999f, 0.9999f),
            actual = actual[1][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.9999f, -1.0000f, -1.0000f),
            actual = actual[1][1][1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `Axis1_train=axis1で正規化および勾配を伝播`() = networkScopeTestRule {
        val delta = Batch.of(
            IOType.d3(2, 2, 3) { i, j, k -> ((i * 6 + j * 3 + k) * (i * 6 + j * 3 + k) % 5).toFloat() },
            IOType.d3(2, 2, 3) { i, j, k -> (((i * 6 + j * 3 + k) * (i * 6 + j * 3 + k) + 2) % 5).toFloat() },
        )
        val actual = with(target1) {
            _train(
                input = input,
                env = GraphEnv(),
                calcDelta = { delta },
            )
        } as Batch<IOType.D3>

        assertContentEquals(
            expected = IOType.d1(-2000.0000f, 0.0000f, 0.0000f),
            actual = actual[0][0][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(2000.0000f, 0.0000f, 0.0000f),
            actual = actual[0][0][1],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.0006f, 0.0002f),
            actual = actual[0][1][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, -0.0006f, -0.0002f),
            actual = actual[0][1][1],
            absoluteTolerance = 1e-3f,
        )

        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.0000f, 0.0000f),
            actual = actual[1][0][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.0000f, 0.0000f),
            actual = actual[1][0][1],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.0000f, 0.0000f),
            actual = actual[1][1][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(0.0000f, 0.0000f, 0.0000f),
            actual = actual[1][1][1],
            absoluteTolerance = 1e-3f,
        )
    }

    @Test
    fun `Axis2_expect=axis2で層正規化`() = networkScopeTestRule {
        val actual = with(target2) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D3>

        assertContentEquals(
            expected = IOType.d1(-1.2247f, 0.0000f, 1.2247f),
            actual = actual[0][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-1.2247f, 0.0000f, 1.2247f),
            actual = actual[0][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.7071f, -0.7071f, 1.4142f),
            actual = actual[0][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-1.2247f, 0.0000f, 1.2247f),
            actual = actual[0][1][1],
            absoluteTolerance = 1e-4f,
        )

        assertContentEquals(
            expected = IOType.d1(-0.9805f, -0.3922f, 1.3728f),
            actual = actual[1][0][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-1.2247f, 0.0000f, 1.2247f),
            actual = actual[1][0][1],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(-1.2247f, 1.2247f, 0.0000f),
            actual = actual[1][1][0],
            absoluteTolerance = 1e-4f,
        )
        assertContentEquals(
            expected = IOType.d1(1.1111f, 0.2020f, -1.3131f),
            actual = actual[1][1][1],
            absoluteTolerance = 1e-4f,
        )
    }

    @Test
    fun `Axis2_train=axis2で正規化および勾配を伝播`() = networkScopeTestRule {
        val delta = Batch.of(
            IOType.d3(2, 2, 3) { i, j, k -> ((i * 6 + j * 3 + k) * (i * 6 + j * 3 + k) % 5).toFloat() },
            IOType.d3(2, 2, 3) { i, j, k -> (((i * 6 + j * 3 + k) * (i * 6 + j * 3 + k) + 2) % 5).toFloat() },
        )
        val actual = with(target2) {
            _train(
                input = input,
                env = GraphEnv(),
                calcDelta = { delta },
            )
        } as Batch<IOType.D3>

        assertContentEquals(
            expected = IOType.d1(0.4082f, -0.8165f, 0.4083f),
            actual = actual[0][0][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(0.2041f, -0.4082f, 0.2041f),
            actual = actual[0][0][1],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(-3.1820f, 3.1820f, 0.0000f),
            actual = actual[0][1][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(1.3608f, -2.7216f, 1.3608f),
            actual = actual[0][1][1],
            absoluteTolerance = 1e-3f,
        )

        assertContentEquals(
            expected = IOType.d1(-0.1697f, 0.2263f, -0.0566f),
            actual = actual[1][0][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(-0.6124f, 1.2247f, -0.6124f),
            actual = actual[1][0][1],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(0.8165f, 0.8165f, -1.6330f),
            actual = actual[1][1][0],
            absoluteTolerance = 1e-3f,
        )
        assertContentEquals(
            expected = IOType.d1(0.7422f, -1.1874f, 0.4453f),
            actual = actual[1][1][1],
            absoluteTolerance = 1e-3f,
        )
    }
}
