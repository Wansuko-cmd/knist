@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.batch.shape
import com.wsr.knist.assertContentEquals
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.get
import com.wsr.knist.batch.i
import com.wsr.knist.batch.j
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.d3
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class D2ExtTest {
    @Test
    fun `toD3=D2バッチを3次元に変換`() = ioTypeTestRule {
        val batch = Batch.of(
            IOType.d2(2, 3) { i, j -> i * 3f + j },
            IOType.d2(2, 3) { i, j -> i * 3f + j + 6f },
        )
        val result = batch.toD3()
        assertContentEquals(IOType.d3(2, 2, 3) { i, j, k -> i * 6f + j * 3f + k }, result)
    }

    @Test
    fun `D3toBatch=3次元をD2バッチに変換`() = ioTypeTestRule {
        val d3 = IOType.d3(2, 2, 3) { i, j, k -> i * 6f + j * 3f + k }
        val batch = d3.toBatch()
        assertEquals(2, batch.size)
        assertEquals(2, batch.i)
        assertEquals(3, batch.j)
        assertContentEquals(IOType.d2(2, 3) { i, j -> i * 3f + j }, batch[0])
        assertContentEquals(IOType.d2(2, 3) { i, j -> i * 3f + j + 6f }, batch[1])
    }

    @Test
    fun `toList=D2バッチをリストに変換`() = ioTypeTestRule {
        val d2a = IOType.d2(2, 2) { i, j -> i * 2f + j }
        val d2b = IOType.d2(2, 2) { i, j -> i * 2f + j + 4f }
        val batch = Batch.of(d2a, d2b)
        val list = batch.toList()
        assertEquals(2, list.size)
        assertContentEquals(d2a, list[0])
        assertContentEquals(d2b, list[1])
    }

    @Test
    fun `slice_axis0=D2バッチのaxis0スライス`() = ioTypeTestRule {
        val batch = Batch.of(
            IOType.d2(3, 2) { i, j -> i * 2f + j },
            IOType.d2(3, 2) { i, j -> i * 2f + j + 6f },
        )
        val result = batch.slice(0..1, axis = 0)
        assertContentEquals(IOType.d2(2, 2) { i, j -> i * 2f + j }, result[0])
        assertContentEquals(IOType.d2(2, 2) { i, j -> i * 2f + j + 6f }, result[1])
    }

    @Test
    fun `slice_axis1=D2バッチのaxis1スライス`() = ioTypeTestRule {
        val batch = Batch.of(
            IOType.d2(2, 3) { i, j -> i * 3f + j },
            IOType.d2(2, 3) { i, j -> i * 3f + j + 6f },
        )
        val result = batch.slice(1..2, axis = 1)
        assertContentEquals(IOType.d2(2, 2) { i, j -> i * 3f + j + 1f }, result[0])
        assertContentEquals(IOType.d2(2, 2) { i, j -> i * 3f + j + 7f }, result[1])
    }

    @Test
    fun `reshapeToD3=D2バッチをD3バッチに変形`() = ioTypeTestRule {
        val batch = Batch.of(
            IOType.d2(2, 4) { i, j -> i * 4f + j },
            IOType.d2(2, 4) { i, j -> i * 4f + j + 8f },
        )
        val result = batch.reshapeToD3(2, 2, 2)
        assertEquals(2, result.size)
        assertEquals(listOf(2, 2, 2), result.shape)
        assertContentEquals(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k }, result[0])
        assertContentEquals(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k + 8f }, result[1])
    }

    @Test
    fun `interleave_axis0=D2バッチのaxis0インターリーブ`() = ioTypeTestRule {
        val batch1 = Batch.of(
            IOType.d2(2, 2) { i, _ -> i * 2f },
            IOType.d2(2, 2) { i, _ -> i * 2f + 4f },
        )
        val batch2 = Batch.of(
            IOType.d2(2, 2) { i, _ -> i * 2f + 1f },
            IOType.d2(2, 2) { i, _ -> i * 2f + 5f },
        )
        val result = batch1.interleave(batch2, axis = 0)
        assertContentEquals(IOType.d2(4, 2) { i, _ -> i.toFloat() }, result[0])
        assertContentEquals(IOType.d2(4, 2) { i, _ -> i + 4f }, result[1])
    }

    @Test
    fun `concat_axis0=D2バッチをaxis0で連結`() = ioTypeTestRule {
        val batch1 = Batch.of(
            IOType.d2(1, 3) { _, j -> j.toFloat() },
            IOType.d2(1, 3) { _, j -> j + 6f },
        )
        val batch2 = Batch.of(
            IOType.d2(1, 3) { _, j -> j + 3f },
            IOType.d2(1, 3) { _, j -> j + 9f },
        )
        val result = batch1.concat(batch2, axis = 0)
        assertContentEquals(IOType.d2(2, 3) { i, j -> i * 3f + j }, result[0])
        assertContentEquals(IOType.d2(2, 3) { i, j -> i * 3f + j + 6f }, result[1])
    }

    @Test
    fun `concat_axis1=D2バッチをaxis1で連結`() = ioTypeTestRule {
        val batch1 = Batch.of(
            IOType.d2(2, 2) { i, j -> i * 4f + j },
            IOType.d2(2, 2) { i, j -> i * 4f + j + 8f },
        )
        val batch2 = Batch.of(
            IOType.d2(2, 2) { i, j -> i * 4f + j + 2f },
            IOType.d2(2, 2) { i, j -> i * 4f + j + 10f },
        )
        val result = batch1.concat(batch2, axis = 1)
        assertContentEquals(IOType.d2(2, 4) { i, j -> i * 4f + j }, result[0])
        assertContentEquals(IOType.d2(2, 4) { i, j -> i * 4f + j + 8f }, result[1])
    }

    @Test
    fun `transpose=D2バッチの転置`() = ioTypeTestRule {
        val batch = Batch.of(
            IOType.d2(2, 3) { i, j -> i * 3f + j },
            IOType.d2(2, 3) { i, j -> i * 3f + j + 6f },
        )
        val result = batch.transpose()
        assertContentEquals(IOType.d2(3, 2) { i, j -> i + j * 3f }, result[0])
        assertContentEquals(IOType.d2(3, 2) { i, j -> i + j * 3f + 6f }, result[1])
    }
}
