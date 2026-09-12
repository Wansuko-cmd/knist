@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.output.mean

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.get
import com.wsr.knist.core.unwrap
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class MeanAbsoluteD2Test {
    @Test
    fun `expect=そのまま返す`() = networkScopeTestRule {
        val target = MeanAbsoluteD2()
        val input = Batch.of(IOType.d2(2, 2) { i, j -> i * 2f + j })

        val actual = with(target) { _expect(input) }

        assertEquals(expected = input, actual = actual)
    }

    @Test
    fun `train=平均絶対誤差`() = networkScopeTestRule {
        val target = MeanAbsoluteD2()
        val input = Batch.of(IOType.d2(2, 2) { i, j -> i * 2f + j })
        val label = Batch.of(IOType.d2(2, 2) { i, j -> 3f - (i * 2f + j) })

        val actual = with(target) { _train(input = input, label = { label }) }
        val loss = actual.loss.unwrap()
        val delta = actual.delta as Batch<IOType.D2>

        assertEquals(expected = 2f, actual = loss)
        assertEquals(expected = -1f, actual = delta[0][0][0].unwrap())
        assertEquals(expected = -1f, actual = delta[0][0][1].unwrap())
        assertEquals(expected = 1f, actual = delta[0][1][0].unwrap())
        assertEquals(expected = 1f, actual = delta[0][1][1].unwrap())
    }
}
