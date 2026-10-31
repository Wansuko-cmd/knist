@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.batch.shape
import com.wsr.knist.assertContentEquals
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.get
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.core.d3
import com.wsr.knist.core.d4
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test
import kotlin.test.assertEquals

class D3ExtTest {
    @Test
    fun `toD4=D3バッチを4次元に変換`() = ioTypeTestRule {
        val batch = Batch.of(
            IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k },
            IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k + 8f },
        )
        val result = batch.toD4()
        assertContentEquals(
            IOType.d4(2, 2, 2, 2) { i, j, k, l -> i * 8f + j * 4f + k * 2f + l },
            result,
        )
    }

    @Test
    fun `D4toBatch=4次元をD3バッチに変換`() = ioTypeTestRule {
        val d4 = IOType.d4(2, 2, 2, 2) { i, j, k, l -> i * 8f + j * 4f + k * 2f + l }
        val batch = d4.toBatch()
        assertEquals(2, batch.size)
        assertContentEquals(
            IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k },
            batch[0],
        )
        assertContentEquals(
            IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k + 8f },
            batch[1],
        )
    }

    @Test
    fun `reshapeToD4=D3バッチをD4バッチに変形`() = ioTypeTestRule {
        val batch = Batch.of(
            IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k },
            IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k + 8f },
        )
        val result = batch.reshapeToD4(2, 2, 2, 1)
        assertEquals(2, result.size)
        assertEquals(listOf(2, 2, 2, 1), result.shape)
        assertContentEquals(IOType.d4(2, 2, 2, 1) { i, j, k, _ -> i * 4f + j * 2f + k }, result[0])
        assertContentEquals(IOType.d4(2, 2, 2, 1) { i, j, k, _ -> i * 4f + j * 2f + k + 8f }, result[1])
    }

    @Test
    fun `reshapeToD2=D3バッチをD2バッチに変形`() = ioTypeTestRule {
        val batch = Batch.of(
            IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k },
            IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k + 8f },
        )
        val result = batch.reshapeToD2(2, 4)
        assertEquals(2, result.size)
        assertEquals(listOf(2, 4), result.shape)
        assertContentEquals(IOType.d2(2, 4) { i, j -> i * 4f + j }, result[0])
        assertContentEquals(IOType.d2(2, 4) { i, j -> i * 4f + j + 8f }, result[1])
    }

    @Test
    fun `toList=D3バッチをリストに変換`() = ioTypeTestRule {
        val d3a = IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k }
        val d3b = IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k + 8f }
        val batch = Batch.of(d3a, d3b)
        val list = batch.toList()
        assertEquals(2, list.size)
        assertContentEquals(d3a, list[0])
        assertContentEquals(d3b, list[1])
    }

    @Test
    fun `concat_axis0=D3バッチをaxis0で連結`() = ioTypeTestRule {
        val batch1 = Batch.of(
            IOType.d3(1, 2, 2) { _, j, k -> j * 2f + k },
            IOType.d3(1, 2, 2) { _, j, k -> j * 2f + k + 8f },
        )
        val batch2 = Batch.of(
            IOType.d3(1, 2, 2) { _, j, k -> j * 2f + k + 4f },
            IOType.d3(1, 2, 2) { _, j, k -> j * 2f + k + 12f },
        )
        val result = batch1.concat(batch2, axis = 0)
        assertContentEquals(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k }, result[0])
        assertContentEquals(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k + 8f }, result[1])
    }

    @Test
    fun `concat_axis1=D3バッチをaxis1で連結`() = ioTypeTestRule {
        val batch1 = Batch.of(
            IOType.d3(2, 1, 2) { i, _, k -> i * 4f + k },
            IOType.d3(2, 1, 2) { i, _, k -> i * 4f + k + 8f },
        )
        val batch2 = Batch.of(
            IOType.d3(2, 1, 2) { i, _, k -> i * 4f + k + 2f },
            IOType.d3(2, 1, 2) { i, _, k -> i * 4f + k + 10f },
        )
        val result = batch1.concat(batch2, axis = 1)
        assertContentEquals(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k }, result[0])
        assertContentEquals(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k + 8f }, result[1])
    }

    @Test
    fun `concat_axis2=D3バッチをaxis2で連結`() = ioTypeTestRule {
        val batch1 = Batch.of(
            IOType.d3(2, 2, 1) { i, j, _ -> i * 4f + j * 2f },
            IOType.d3(2, 2, 1) { i, j, _ -> i * 4f + j * 2f + 8f },
        )
        val batch2 = Batch.of(
            IOType.d3(2, 2, 1) { i, j, _ -> i * 4f + j * 2f + 1f },
            IOType.d3(2, 2, 1) { i, j, _ -> i * 4f + j * 2f + 9f },
        )
        val result = batch1.concat(batch2, axis = 2)
        assertContentEquals(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k }, result[0])
        assertContentEquals(IOType.d3(2, 2, 2) { i, j, k -> i * 4f + j * 2f + k + 8f }, result[1])
    }
}
