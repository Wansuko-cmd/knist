@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.output.mean

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d3
import com.wsr.knist.core.get
import com.wsr.knist.core.unwrap
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class MeanAbsoluteD3Test {
    @Test
    fun `expect=そのまま返す`() = networkScopeTestRule {
        val target = MeanAbsoluteD3()
        val input = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k })

        val actual = with(target) { _expect(input) }

        assertEquals(expected = input, actual = actual)
    }

    @Test
    fun `train=平均絶対誤差`() = networkScopeTestRule {
        val target = MeanAbsoluteD3()
        val input = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k })
        val label = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> 7f - (i * 4f + j * 2f + k) })

        val actual = with(target) { _train(input = input, label = { label }) }
        val loss = actual.loss.unwrap()
        val delta = actual.delta as Batch<IOType.D3>

        assertEquals(expected = 4f, actual = loss)
        assertEquals(expected = -1f, actual = delta[0][0][0][0].unwrap())
        assertEquals(expected = -1f, actual = delta[0][0][0][1].unwrap())
        assertEquals(expected = -1f, actual = delta[0][0][1][0].unwrap())
        assertEquals(expected = -1f, actual = delta[0][0][1][1].unwrap())
        assertEquals(expected = 1f, actual = delta[0][1][0][0].unwrap())
        assertEquals(expected = 1f, actual = delta[0][1][0][1].unwrap())
        assertEquals(expected = 1f, actual = delta[0][1][1][0].unwrap())
        assertEquals(expected = 1f, actual = delta[0][1][1][1].unwrap())
    }
}
