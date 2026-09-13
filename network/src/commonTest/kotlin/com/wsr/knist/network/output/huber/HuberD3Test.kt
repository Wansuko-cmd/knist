@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.knist.network.output.huber

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d3
import com.wsr.knist.core.get
import com.wsr.knist.core.unwrap
import com.wsr.knist.network.networkScopeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class HuberD3Test {
    @Test
    fun `expect=そのまま返す`() = networkScopeTestRule {
        val target = HuberD3(threshold = 1f)
        val input = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k })

        val actual = with(target) { _expect(input) }

        assertEquals(expected = input, actual = actual)
    }

    @Test
    fun `train=閾値未満は二乗誤差、閾値以上は絶対誤差`() = networkScopeTestRule {
        val target = HuberD3(threshold = 1f)
        val input = Batch.of(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k })
        val label = Batch.of(
            IOType.d3(2, 2, 2) { i, j, k ->
                when (Triple(i, j, k)) {
                    Triple(0, 0, 0) -> -0.5f
                    Triple(0, 0, 1) -> 3f
                    Triple(0, 1, 0) -> -1f
                    Triple(0, 1, 1) -> 3.5f
                    Triple(1, 0, 0) -> 3.3f
                    Triple(1, 0, 1) -> 6.5f
                    Triple(1, 1, 0) -> 4f
                    else -> 7.3f
                }
            },
        )

        val actual = with(target) { _train(input = input, label = { label }) }
        val loss = actual.loss.unwrap()
        val delta = actual.delta as Batch<IOType.D3>

        assertEquals(expected = 0.88f, actual = loss, absoluteTolerance = 1e-4f)
        assertEquals(expected = 0.5f, actual = delta[0][0][0][0].unwrap())
        assertEquals(expected = -1f, actual = delta[0][0][0][1].unwrap())
        assertEquals(expected = 1f, actual = delta[0][0][1][0].unwrap())
        assertEquals(expected = -0.5f, actual = delta[0][0][1][1].unwrap())
        assertEquals(expected = 0.7f, actual = delta[0][1][0][0].unwrap(), absoluteTolerance = 1e-4f)
        assertEquals(expected = -1f, actual = delta[0][1][0][1].unwrap())
        assertEquals(expected = 1f, actual = delta[0][1][1][0].unwrap())
        assertEquals(expected = -0.3f, actual = delta[0][1][1][1].unwrap(), absoluteTolerance = 1e-4f)
    }
}
