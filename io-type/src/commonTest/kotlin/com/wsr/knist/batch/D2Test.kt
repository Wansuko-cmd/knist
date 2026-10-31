@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.batch
import com.wsr.knist.assertContentEquals
import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d2
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class D2Test {
    @Test
    fun `i_j=D2バッチの次元`() = ioTypeTestRule {
        val batch = Batch.of(
            IOType.d2(2, 3) { _, _ -> 0f },
            IOType.d2(2, 3) { _, _ -> 1f },
        )
        assertEquals(2, batch.i)
        assertEquals(3, batch.j)
    }

    @Test
    fun `get=D2バッチ要素取得`() = ioTypeTestRule {
        val d2a = IOType.d2(2, 3) { i, j -> i * 3f + j }
        val d2b = IOType.d2(2, 3) { i, j -> i * 3f + j + 6f }
        val batch = Batch.of(d2a, d2b)
        assertContentEquals(d2a, batch[0])
        assertContentEquals(d2b, batch[1])
    }
}
