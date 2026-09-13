@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.process.compute.function.softmax

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d2
import com.wsr.knist.core.get
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.assertContentEquals
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test

class SoftmaxAxisD2Test {
    val target0 get() = SoftmaxAxisD2(inputI = 2, inputJ = 2, axis = 0)
    val target1 get() = SoftmaxAxisD2(inputI = 2, inputJ = 2, axis = 1)

    val input
        get() = Batch.of(
            IOType.d2(2, 2) { i, j -> floatArrayOf(1f, 2f, -1f, 0.5f)[i * 2 + j] },
            IOType.d2(2, 2) { i, j -> floatArrayOf(0f, -0.5f, 1.5f, 2f)[i * 2 + j] },
        )

    @Test
    fun `Axis0_expect=axis0で正規化`() = networkScopeTestRule {
        val actual = with(target0) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D2>

        assertContentEquals(expected = IOType.d1(0.8808f, 0.8176f), actual = actual[0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.1192f, 0.1824f), actual = actual[0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.1824f, 0.0759f), actual = actual[1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.8176f, 0.9241f), actual = actual[1][1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis0_train=axis0でのvjpを返す`() = networkScopeTestRule {
        val actual = with(target0) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D2>

        assertContentEquals(expected = IOType.d1(0.0800f, 0.0947f), actual = actual[0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.0800f, -0.0947f), actual = actual[0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.0947f, -0.0595f), actual = actual[1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.0947f, 0.0595f), actual = actual[1][1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis1_expect=axis1で正規化`() = networkScopeTestRule {
        val actual = with(target1) { _expect(input = input, env = GraphEnv()) } as Batch<IOType.D2>

        assertContentEquals(expected = IOType.d1(0.2689f, 0.7311f), actual = actual[0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.1824f, 0.8176f), actual = actual[0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.6225f, 0.3775f), actual = actual[1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.3775f, 0.6225f), actual = actual[1][1], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis1_train=axis1でのvjpを返す`() = networkScopeTestRule {
        val actual = with(target1) {
            _train(input = input, env = GraphEnv(), calcDelta = { it })
        } as Batch<IOType.D2>

        assertContentEquals(expected = IOType.d1(-0.0909f, 0.0909f), actual = actual[0][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.0947f, 0.0947f), actual = actual[0][1], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(0.0576f, -0.0576f), actual = actual[1][0], absoluteTolerance = 1e-4f)
        assertContentEquals(expected = IOType.d1(-0.0576f, 0.0576f), actual = actual[1][1], absoluteTolerance = 1e-4f)
    }
}
