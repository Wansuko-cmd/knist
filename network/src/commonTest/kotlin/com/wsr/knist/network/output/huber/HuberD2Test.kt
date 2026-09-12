@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.output.huber

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.get
import com.wsr.knist.core.unwrap
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class HuberD2Test {
    @Test
    fun `expect=そのまま返す`() = networkScopeTestRule {
        val target = HuberD2(threshold = 1f)
        val input = Batch.of(IOType.d2(2, 2) { i, j -> i * 2f + j })

        val actual = with(target) { _expect(input) }

        assertEquals(expected = input, actual = actual)
    }

    @Test
    fun `train=閾値未満は二乗誤差、閾値以上は絶対誤差`() = networkScopeTestRule {
        val target = HuberD2(threshold = 1f)
        val input = Batch.of(IOType.d2(2, 2) { i, j -> i * 2f + j })
        val label = Batch.of(
            IOType.d2(2, 2) { i, j ->
                when (i to j) {
                    0 to 0 -> -0.5f
                    0 to 1 -> 3f
                    1 to 0 -> -1f
                    else -> 3.5f
                }
            },
        )

        val actual = with(target) { _train(input = input, label = { label }) }
        val loss = actual.loss.unwrap()
        val delta = actual.delta as Batch<IOType.D2>

        assertEquals(expected = 1.0625f, actual = loss, absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.5f, actual = delta[0][0][0].unwrap())
        assertEquals(expected = -1f, actual = delta[0][0][1].unwrap())
        assertEquals(expected = 1f, actual = delta[0][1][0].unwrap())
        assertEquals(expected = -0.5f, actual = delta[0][1][1].unwrap())
    }
}
