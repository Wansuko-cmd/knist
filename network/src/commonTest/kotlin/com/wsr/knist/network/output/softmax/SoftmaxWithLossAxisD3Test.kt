@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.output.softmax

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d3
import com.wsr.knist.core.get
import com.wsr.knist.core.unwrap
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class SoftmaxWithLossAxisD3Test {
    val input
        get() = Batch.of(
            IOType.d3(2, 2, 2) { i, j, k -> floatArrayOf(1f, 2f, -1f, 0.5f, 0f, -0.5f, 1.5f, 2f)[i * 4 + j * 2 + k] },
        )
    val label
        get() = Batch.of(
            IOType.d3(2, 2, 2) { i, j, k -> (i * 4f + j * 2f + k) * 2f },
        )

    @Test
    fun `Axis0_expect=axis0で正規化`() = networkScopeTestRule {
        val target = SoftmaxWithLossAxisD3(outputI = 2, outputJ = 2, outputK = 2, axis = 0, temperature = 0.8f)
        val actual = with(target) { _expect(input) } as Batch<IOType.D3>

        assertEquals(expected = 0.7773f, actual = actual[0][0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.9579f, actual = actual[0][0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0421f, actual = actual[0][0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.1330f, actual = actual[0][0][1][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.2227f, actual = actual[0][1][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0421f, actual = actual[0][1][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.9579f, actual = actual[0][1][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.8670f, actual = actual[0][1][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis0_train=softmaxの逆伝播`() = networkScopeTestRule {
        val target = SoftmaxWithLossAxisD3(outputI = 2, outputJ = 2, outputK = 2, axis = 0, temperature = 0.8f)
        val actual = with(target) { _train(input = input, label = { label }) }
        val loss = actual.loss.unwrap()
        val delta = actual.delta as Batch<IOType.D3>

        assertEquals(expected = -1.6107f, actual = loss, absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.9716f, actual = delta[0][0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -1.3026f, actual = delta[0][0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -4.9474f, actual = delta[0][0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -7.3338f, actual = delta[0][0][1][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -9.7216f, actual = delta[0][1][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -12.4474f, actual = delta[0][1][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -13.8026f, actual = delta[0][1][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -16.4162f, actual = delta[0][1][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis1_expect=axis1で正規化`() = networkScopeTestRule {
        val target = SoftmaxWithLossAxisD3(outputI = 2, outputJ = 2, outputK = 2, axis = 1, temperature = 0.8f)
        val actual = with(target) { _expect(input) } as Batch<IOType.D3>

        assertEquals(expected = 0.9241f, actual = actual[0][0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.8670f, actual = actual[0][0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0759f, actual = actual[0][0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.1330f, actual = actual[0][0][1][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.1330f, actual = actual[0][1][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0421f, actual = actual[0][1][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.8670f, actual = actual[0][1][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.9579f, actual = actual[0][1][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis1_train=softmaxの逆伝播`() = networkScopeTestRule {
        val target = SoftmaxWithLossAxisD3(outputI = 2, outputJ = 2, outputK = 2, axis = 1, temperature = 0.8f)
        val actual = with(target) { _train(input = input, label = { label }) }
        val loss = actual.loss.unwrap()
        val delta = actual.delta as Batch<IOType.D3>

        assertEquals(expected = -1.2007f, actual = loss, absoluteTolerance = 1e-4f)
        assertEquals(expected = 1.1552f, actual = delta[0][0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -1.4162f, actual = delta[0][0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -4.9052f, actual = delta[0][0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -7.3338f, actual = delta[0][0][1][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -9.8338f, actual = delta[0][1][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -12.4474f, actual = delta[0][1][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -13.9162f, actual = delta[0][1][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -16.3026f, actual = delta[0][1][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis2_expect=axis2で正規化`() = networkScopeTestRule {
        val target = SoftmaxWithLossAxisD3(outputI = 2, outputJ = 2, outputK = 2, axis = 2, temperature = 0.8f)
        val actual = with(target) { _expect(input) } as Batch<IOType.D3>

        assertEquals(expected = 0.2227f, actual = actual[0][0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.7773f, actual = actual[0][0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.1330f, actual = actual[0][0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.8670f, actual = actual[0][0][1][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.6514f, actual = actual[0][1][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.3486f, actual = actual[0][1][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.3486f, actual = actual[0][1][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.6514f, actual = actual[0][1][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }

    @Test
    fun `Axis2_train=softmaxの逆伝播`() = networkScopeTestRule {
        val target = SoftmaxWithLossAxisD3(outputI = 2, outputJ = 2, outputK = 2, axis = 2, temperature = 0.8f)
        val actual = with(target) { _train(input = input, label = { label }) }
        val loss = actual.loss.unwrap()
        val delta = actual.delta as Batch<IOType.D3>

        assertEquals(expected = -1.7347f, actual = loss, absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.2784f, actual = delta[0][0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -1.5284f, actual = delta[0][0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -4.8338f, actual = delta[0][0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -6.4162f, actual = delta[0][0][1][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -9.1858f, actual = delta[0][1][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -12.0642f, actual = delta[0][1][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -14.5642f, actual = delta[0][1][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -16.6858f, actual = delta[0][1][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }
}
