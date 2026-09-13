@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.output.sigmoid

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d3
import com.wsr.knist.core.get
import com.wsr.knist.core.unwrap
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class SigmoidWithLossD3Test {
    @Test
    fun `expect=sigmoidを計算`() = networkScopeTestRule {
        val target = SigmoidWithLossD3(outputI = 2, outputJ = 2, outputK = 2)
        val input = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k })

        val actual = with(target) { _expect(input) } as Batch<IOType.D3>

        assertEquals(expected = 0.5000f, actual = actual[0][0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.7311f, actual = actual[0][0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.8808f, actual = actual[0][0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.9526f, actual = actual[0][0][1][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.9820f, actual = actual[0][1][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.9933f, actual = actual[0][1][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.9975f, actual = actual[0][1][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.9991f, actual = actual[0][1][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }

    @Test
    fun `train=sigmoidの逆伝播`() = networkScopeTestRule {
        val target = SigmoidWithLossD3(outputI = 2, outputJ = 2, outputK = 2)
        val input = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k })
        val label = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> i * 8f + j * 4f + k * 2f })

        val actual = with(target) { _train(input = input, label = { label }) }
        val loss = actual.loss.unwrap()
        val delta = actual.delta as Batch<IOType.D3>

        assertEquals(expected = -250.7878f, actual = loss, absoluteTolerance = 1e-3f)
        assertEquals(expected = 0.5000f, actual = delta[0][0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -1.2689f, actual = delta[0][0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -3.1192f, actual = delta[0][0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -5.0474f, actual = delta[0][0][1][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -7.0180f, actual = delta[0][1][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -9.0067f, actual = delta[0][1][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -11.0025f, actual = delta[0][1][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -13.0009f, actual = delta[0][1][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }
}
