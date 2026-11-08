@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.core.shape

import com.wsr.knist.assertContentEquals
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d1
import com.wsr.knist.core.d2
import com.wsr.knist.ioTypeTestRule
import kotlin.test.Test

class ShapeD1Test {
    @Test
    fun `broadcastToD2_axis0=1次元を2次元にブロードキャスト`() = ioTypeTestRule {
        val d1 = IOType.d1(listOf(1f, 2f, 3f))
        val actual = d1.broadcastToD2(axis = 0, size = 2)
        assertContentEquals(
            expected = IOType.d2(2, 3) { _, j -> j + 1f },
            actual = actual,
        )
    }

    @Test
    fun `broadcastToD2_axis1=1次元を2次元にブロードキャスト`() = ioTypeTestRule {
        val d1 = IOType.d1(listOf(1f, 2f, 3f))
        val actual = d1.broadcastToD2(axis = 1, size = 2)
        assertContentEquals(
            expected = IOType.d2(3, 2) { i, _ -> i + 1f },
            actual = actual,
        )
    }

    @Test
    fun `reshapeToD2=1次元を2次元に変換`() = ioTypeTestRule {
        val d1 = IOType.d1(listOf(0f, 1f, 2f, 3f, 4f, 5f))
        val actual = d1.reshapeToD2(2, 3)
        assertContentEquals(
            expected = IOType.d2(2, 3) { i, j -> i * 3f + j },
            actual = actual,
        )
    }

    @Test
    fun `slice=指定範囲を1次元スライス`() = ioTypeTestRule {
        val d1 = IOType.d1(listOf(0f, 1f, 2f, 3f, 4f))
        val actual = d1.slice(1..3)
        assertContentEquals(
            expected = IOType.d1(listOf(1f, 2f, 3f)),
            actual = actual,
        )
    }

    @Test
    fun `interleave=2つの1次元をインターリーブ`() = ioTypeTestRule {
        val d1a = IOType.d1(listOf(0f, 2f, 4f))
        val d1b = IOType.d1(listOf(1f, 3f, 5f))
        val actual = d1a.interleave(d1b)
        assertContentEquals(
            expected = IOType.d1(listOf(0f, 1f, 2f, 3f, 4f, 5f)),
            actual = actual,
        )
    }
}
