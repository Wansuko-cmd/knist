@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.output.mean

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.get
import com.wsr.knist.core.unwrap
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class MeanAbsoluteD1Test {
    @Test
    fun `expect=そのまま返す`() = networkScopeTestRule {
        val target = MeanAbsoluteD1()
        val input = Batch.of(IOType.d1(1f, 2f, 3f))

        val actual = with(target) { _expect(input) }

        assertEquals(expected = input, actual = actual)
    }

    @Test
    fun `train=平均絶対誤差`() = networkScopeTestRule {
        val target = MeanAbsoluteD1()
        val input = Batch.of(IOType.d1(1f, 2f, 3f))
        val label = Batch.of(IOType.d1(0f, 3f, 1f))

        val actual = with(target) { _train(input = input, label = { label }) }
        val loss = actual.loss.unwrap()
        val delta = actual.delta as Batch<IOType.D1>

        assertEquals(expected = 1.3333f, actual = loss, absoluteTolerance = 1e-4f)
        assertEquals(expected = 1f, actual = delta[0][0].unwrap())
        assertEquals(expected = -1f, actual = delta[0][1].unwrap())
        assertEquals(expected = 1f, actual = delta[0][2].unwrap())
    }
}
