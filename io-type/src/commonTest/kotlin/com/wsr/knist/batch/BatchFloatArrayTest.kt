@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.batch

import com.wsr.knist.BufferScope
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BatchFloatArrayTest {
    private fun values(n: Int) = FloatArray(n) { it.toFloat() }

    @Test
    fun `global=FloatArrayからD0のバッチを作れる`() = ioTypeTestRule {
        val batch = Batch.d0(size = 3, value = values(3))
        assertEquals(listOf(1), batch.shape)
        assertEquals(3, batch.size)
        assertEquals(values(3).toList(), batch.unwrap().toList())
    }

    @Test
    fun `global=FloatArrayからD1からD4のバッチをshape指定で作れる`() = ioTypeTestRule {
        assertEquals(listOf(3), Batch.d1(size = 2, shape = listOf(3), value = values(6)).shape)
        assertEquals(listOf(2, 3), Batch.d2(size = 2, shape = listOf(2, 3), value = values(12)).shape)
        assertEquals(listOf(2, 2, 2), Batch.d3(size = 2, shape = listOf(2, 2, 2), value = values(16)).shape)
        assertEquals(listOf(2, 2, 2, 2), Batch.d4(size = 2, shape = listOf(2, 2, 2, 2), value = values(32)).shape)
    }

    @Test
    fun `global=FloatArrayからD3とD4のバッチを次元指定で作れる`() = ioTypeTestRule {
        val d3 = Batch.d3(size = 2, i = 2, j = 2, k = 2, value = values(16))
        assertEquals(listOf(2, 2, 2), d3.shape)
        assertEquals(values(16).toList(), d3.unwrap().toList())

        val d4 = Batch.d4(size = 2, i = 2, j = 2, k = 2, l = 2, value = values(32))
        assertEquals(listOf(2, 2, 2, 2), d4.shape)
        assertEquals(values(32).toList(), d4.unwrap().toList())
    }

    @Test
    fun `global=scope外ではbufferScopeに登録されない`() = ioTypeTestRule {
        val batch = Batch.d3(size = 2, i = 2, j = 2, k = 2, value = values(16))
        assertFalse(BufferScope().buffers.contains(batch.value))
    }

    @Test
    fun `local=scope内ではFloatArrayから作ったバッチがbufferScopeに登録される`() = ioTypeTestRule {
        val bufferScope = BufferScope()
        val ioScope = IOScope(bufferScope)
        val batch: Batch<IOType.D3.Local> = with(ioScope) { Batch.d3(size = 2, i = 2, j = 2, k = 2, value = values(16)) }
        assertTrue(bufferScope.buffers.contains(batch.value))
        assertEquals(values(16).toList(), batch.unwrap().toList())
    }

    @Test
    fun `unwrap=返されたFloatArrayを書き換えても元の値は変わらない`() = ioTypeTestRule {
        val batch = Batch.of(IOType.d1(listOf(1f, 2f)), IOType.d1(listOf(3f, 4f)))
        batch.unwrap()[0] = 99f
        assertEquals(listOf(1f, 2f, 3f, 4f), batch.unwrap().toList())
    }
}
