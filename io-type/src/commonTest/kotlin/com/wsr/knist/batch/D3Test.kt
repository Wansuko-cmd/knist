@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.batch
import com.wsr.knist.assertContentEquals
import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d3
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class D3Test {
    @Test
    fun `i_j_k=D3バッチの次元`() = ioTypeTestRule {
        val batch = Batch.of(
            IOType.d3(2, 3, 4) { _, _, _ -> 0f },
            IOType.d3(2, 3, 4) { _, _, _ -> 1f },
        )
        assertEquals(2, batch.i)
        assertEquals(3, batch.j)
        assertEquals(4, batch.k)
    }

    @Test
    fun `get=D3バッチ要素取得`() = ioTypeTestRule {
        val d3a = IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k }
        val d3b = IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k + 8f }
        val batch = Batch.of(d3a, d3b)
        assertContentEquals(d3a, batch[0])
        assertContentEquals(d3b, batch[1])
    }
}
