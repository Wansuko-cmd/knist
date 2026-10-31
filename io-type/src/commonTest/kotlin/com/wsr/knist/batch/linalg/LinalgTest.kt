@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.batch.linalg
import com.wsr.knist.assertContentEquals
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.get
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d0
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test

class LinalgTest {
    @Test
    fun `D1バッチ内積=バッチごとに内積を計算`() = ioTypeTestRule {
        val batch1 = Batch.of(
            IOType.d1(listOf(1f, 2f, 3f)),
            IOType.d1(listOf(4f, 5f, 6f)),
        )
        val batch2 = Batch.of(
            IOType.d1(listOf(1f, 0f, 0f)),
            IOType.d1(listOf(0f, 1f, 0f)),
        )
        val result = batch1 inner batch2
        assertContentEquals(IOType.d0(1f), result[0])
        assertContentEquals(IOType.d0(5f), result[1])
    }

    @Test
    fun `D1バッチ matMul D2=バッチベクトルと行列の積`() = ioTypeTestRule {
        val d2 = IOType.d2(3, 2) { i, j -> i * 2f + j }
        val batch = Batch.of(
            IOType.d1(listOf(1f, 0f, 0f)),
            IOType.d1(listOf(0f, 1f, 0f)),
        )
        val result = batch.matMul(d2)
        assertContentEquals(IOType.d1(listOf(0f, 1f)), result[0])
        assertContentEquals(IOType.d1(listOf(2f, 3f)), result[1])
    }

    @Test
    fun `D1バッチ matMul D2を転置=バッチベクトルと転置行列の積`() = ioTypeTestRule {
        val d2 = IOType.d2(2, 3) { i, j -> i * 3f + j }
        val batch = Batch.of(
            IOType.d1(listOf(1f, 0f, 0f)),
            IOType.d1(listOf(0f, 1f, 0f)),
        )
        val result = batch.matMul(d2, trans = true)
        assertContentEquals(IOType.d1(listOf(0f, 3f)), result[0])
        assertContentEquals(IOType.d1(listOf(1f, 4f)), result[1])
    }

    @Test
    fun `D2バッチを転置 matMul D2=バッチごとに転置してから行列積`() = ioTypeTestRule {
        val batchD2 = Batch.of(
            IOType.d2(2, 3) { i, j -> i * 3f + j },
            IOType.d2(2, 3) { i, j -> i * 3f + j + 6f },
        )
        val d2 = IOType.d2(2, 2) { i, j -> i * 2f + j }
        val result = batchD2.matMul(d2, transA = true)
        assertContentEquals(
            IOType.d2(listOf(3, 2), listOf(6f, 9f, 8f, 13f, 10f, 17f)),
            result[0],
        )
        assertContentEquals(
            IOType.d2(listOf(3, 2), listOf(18f, 33f, 20f, 37f, 22f, 41f)),
            result[1],
        )
    }

    @Test
    fun `D2バッチ matMul D2=バッチ行列積`() = ioTypeTestRule {
        val batchD2 = Batch.of(
            IOType.d2(2, 3) { i, j -> i * 3f + j },
            IOType.d2(2, 3) { i, j -> i * 3f + j + 6f },
        )
        val d2 = IOType.d2(3, 2) { i, j -> i * 2f + j }
        val result = batchD2.matMul(d2)
        assertContentEquals(
            IOType.d2(listOf(2, 2), listOf(10f, 13f, 28f, 40f)),
            result[0],
        )
        assertContentEquals(
            IOType.d2(listOf(2, 2), listOf(46f, 67f, 64f, 94f)),
            result[1],
        )
    }

    @Test
    fun `D2バッチ matMul D2バッチ=バッチごとの行列積`() = ioTypeTestRule {
        val batchA = Batch.of(
            IOType.d2(2, 3) { i, j -> i * 3f + j },
            IOType.d2(2, 3) { i, j -> i * 3f + j + 6f },
        )
        val batchB = Batch.of(
            IOType.d2(3, 2) { i, j -> i * 2f + j },
            IOType.d2(3, 2) { i, j -> i * 2f + j },
        )
        val result = batchA.matMul(batchB)
        assertContentEquals(
            IOType.d2(listOf(2, 2), listOf(10f, 13f, 28f, 40f)),
            result[0],
        )
        assertContentEquals(
            IOType.d2(listOf(2, 2), listOf(46f, 67f, 64f, 94f)),
            result[1],
        )
    }
}
