@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.output.softmax

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.get
import com.wsr.knist.core.unwrap
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class SoftmaxWithLossAxisD2Test {
    @Test
    fun `expect=axisで指定した軸に対するsoftmaxを計算`() = networkScopeTestRule {
        val target = SoftmaxWithLossAxisD2(outputI = 2, outputJ = 2, axis = 1, temperature = 0.8f)
        val input = Batch.of(IOType.d2(2, 2) { i, j -> i * 2f + j })

        val actual = with(target) { _expect(input) } as Batch<IOType.D2>

        assertEquals(expected = 0.2227f, actual = actual[0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.7773f, actual = actual[0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.2227f, actual = actual[0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.7773f, actual = actual[0][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }

    @Test
    fun `train=softmaxの逆伝播`() = networkScopeTestRule {
        val target = SoftmaxWithLossAxisD2(outputI = 2, outputJ = 2, axis = 1, temperature = 0.8f)
        val input = Batch.of(IOType.d2(2, 2) { i, j -> i * 2f + j })
        val label = Batch.of(IOType.d2(2, 2) { i, j -> i * 4f + j * 2f })

        val actual = with(target) { _train(input = input, label = { label }) }
        val loss = actual.loss.unwrap()
        val delta = actual.delta as Batch<IOType.D2>

        assertEquals(expected = -1.0779f, actual = loss, absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.2784f, actual = delta[0][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -1.5284f, actual = delta[0][0][1].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -4.7216f, actual = delta[0][1][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -6.5284f, actual = delta[0][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }
}
