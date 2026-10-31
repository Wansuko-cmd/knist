@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.batch
import com.wsr.knist.assertContentEquals
import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d4
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class D4Test {
    @Test
    fun `i_j_k_l=D4バッチの次元`() = ioTypeTestRule {
        val batch = Batch.of(
            IOType.d4(2, 3, 4, 5) { _, _, _, _ -> 0f },
            IOType.d4(2, 3, 4, 5) { _, _, _, _ -> 1f },
        )
        assertEquals(2, batch.i)
        assertEquals(3, batch.j)
        assertEquals(4, batch.k)
        assertEquals(5, batch.l)
    }

    @Test
    fun `get=D4バッチ要素取得`() = ioTypeTestRule {
        val d4a = IOType.d4(2, 2, 2, 2) { i, j, k, l -> i * 8f + j * 4f + k * 2f + l }
        val d4b = IOType.d4(2, 2, 2, 2) { i, j, k, l -> i * 8f + j * 4f + k * 2f + l + 16f }
        val batch = Batch.of(d4a, d4b)
        assertContentEquals(d4a, batch[0])
        assertContentEquals(d4b, batch[1])
    }
}
