@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.output.softmax

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.get
import com.wsr.knist.core.unwrap
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class SoftmaxWithLossD2Test {
    @Test
    fun `expect=i_jすべてを含む全要素に対するsoftmaxを計算`() = networkScopeTestRule {
        val target = SoftmaxWithLossD2(outputI = 2, outputJ = 2, temperature = 0.8f)
        val input = Batch.of(IOType.d2(2, 2) { i, j -> i * 2f + j })

        val actual = with(target) { _expect(input) } as Batch<IOType.D2>

        assertEquals(expected = 0.0169f, actual = actual[0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0590f, actual = actual[0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.2058f, actual = actual[0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.7183f, actual = actual[0][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }

    @Test
    fun `train=softmaxの逆伝播`() = networkScopeTestRule {
        val target = SoftmaxWithLossD2(outputI = 2, outputJ = 2, temperature = 0.8f)
        val input = Batch.of(IOType.d2(2, 2) { i, j -> i * 2f + j })
        val label = Batch.of(IOType.d2(2, 2) { i, j -> i * 4f + j * 2f })

        val actual = with(target) { _train(input = input, label = { label }) }
        val loss = actual.loss.unwrap()
        val delta = actual.delta as Batch<IOType.D2>

        assertEquals(expected = -1.6585f, actual = loss, absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.0211f, actual = delta[0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -2.4263f, actual = delta[0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -4.7427f, actual = delta[0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -6.6021f, actual = delta[0][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }
}
