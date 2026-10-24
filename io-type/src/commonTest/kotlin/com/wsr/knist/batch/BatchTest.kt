@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.batch
import com.wsr.knist.assertContentEquals
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d0
import com.wsr.knist.core.d1
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class BatchTest {
    @Test
    fun `batchOf=D0バッチのサイズと形状`() = ioTypeTestRule {
        val batch = Batch.of(IOType.d0(1f), IOType.d0(2f), IOType.d0(3f))
        assertEquals(3, batch.size)
        assertEquals(listOf(1), batch.shape)
    }

    @Test
    fun `batchOf=D1バッチのサイズと形状`() = ioTypeTestRule {
        val batch = Batch.of(
            IOType.d1(listOf(1f, 2f, 3f)),
            IOType.d1(listOf(4f, 5f, 6f)),
        )
        assertEquals(2, batch.size)
        assertEquals(listOf(3), batch.shape)
    }

    @Test
    fun `Batch init=初期化関数でバッチ作成`() = ioTypeTestRule {
        val batch = Batch.d0(size = 3) { i -> i.toFloat() }
        assertEquals(3, batch.size)
        assertContentEquals(IOType.d0(0f), batch[0])
        assertContentEquals(IOType.d0(1f), batch[1])
        assertContentEquals(IOType.d0(2f), batch[2])
    }

    @Test
    fun `step=バッチのstepが形状から計算される`() = ioTypeTestRule {
        val batch = Batch.of(
            IOType.d1(listOf(1f, 2f, 3f)),
            IOType.d1(listOf(4f, 5f, 6f)),
        )
        assertEquals(3, batch.step)
    }

    @Test
    fun `Batch init=サイズ0でも例外にならず形状を保つ`() = ioTypeTestRule {
        val batch = Batch.d1(size = 0, i = 3) { j -> j.toFloat() }
        assertEquals(0, batch.size)
        assertEquals(listOf(3), batch.shape)
    }
}
