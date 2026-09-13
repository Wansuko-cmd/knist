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

class SoftmaxWithLossD3Test {
    @Test
    fun `expect=i_j_kすべてを含む全要素に対するsoftmaxを計算`() = networkScopeTestRule {
        val target = SoftmaxWithLossD3(outputI = 2, outputJ = 2, outputK = 2, temperature = 0.8f)
        val input = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k })

        val actual = with(target) { _expect(input) } as Batch<IOType.D3>

        assertEquals(expected = 0.0001f, actual = actual[0][0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0004f, actual = actual[0][0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0014f, actual = actual[0][0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0048f, actual = actual[0][0][1][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0168f, actual = actual[0][1][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0586f, actual = actual[0][1][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.2044f, actual = actual[0][1][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.7135f, actual = actual[0][1][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }

    @Test
    fun `train=softmaxの逆伝播`() = networkScopeTestRule {
        val target = SoftmaxWithLossD3(outputI = 2, outputJ = 2, outputK = 2, temperature = 0.8f)
        val input = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k })
        val label = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> i * 8f + j * 4f + k * 2f })

        val actual = with(target) { _train(input = input, label = { label }) }
        val loss = actual.loss.unwrap()
        val delta = actual.delta as Batch<IOType.D3>

        assertEquals(expected = -2.5800f, actual = loss, absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0001f, actual = delta[0][0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -2.4995f, actual = delta[0][0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -4.9983f, actual = delta[0][0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -7.4940f, actual = delta[0][0][1][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -9.9790f, actual = delta[0][1][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -12.4268f, actual = delta[0][1][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -14.7445f, actual = delta[0][1][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -16.6081f, actual = delta[0][1][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }
}
