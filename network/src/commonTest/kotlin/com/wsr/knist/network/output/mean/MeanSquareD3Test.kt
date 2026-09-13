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

class MeanSquareD3Test {
    @Test
    fun `expect=そのまま返す`() = networkScopeTestRule {
        val target = MeanSquareD3()
        val input = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k })

        val actual = with(target) { _expect(input) }

        assertEquals(expected = input, actual = actual)
    }

    @Test
    fun `train=二乗平均誤差`() = networkScopeTestRule {
        val target = MeanSquareD3()
        val input = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k })
        val label = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> i * 8f + j * 4f + k * 2f })

        val actual = with(target) { _train(input = input, label = { label }) }
        val loss = actual.loss.unwrap()
        val delta = actual.delta as Batch<IOType.D3>

        assertEquals(expected = 8.75f, actual = loss)
        assertEquals(expected = 0f, actual = delta[0][0][0][0].unwrap())
        assertEquals(expected = -1f, actual = delta[0][0][0][1].unwrap())
        assertEquals(expected = -2f, actual = delta[0][0][1][0].unwrap())
        assertEquals(expected = -3f, actual = delta[0][0][1][1].unwrap())
        assertEquals(expected = -4f, actual = delta[0][1][0][0].unwrap())
        assertEquals(expected = -5f, actual = delta[0][1][0][1].unwrap())
        assertEquals(expected = -6f, actual = delta[0][1][1][0].unwrap())
        assertEquals(expected = -7f, actual = delta[0][1][1][1].unwrap())
    }
}
