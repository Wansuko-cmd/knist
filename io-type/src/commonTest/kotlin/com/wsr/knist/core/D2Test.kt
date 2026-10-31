@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.core

import com.wsr.knist.assertContentEquals
import kotlin.test.Test
import kotlin.test.assertEquals

class D2Test {
    @Test
    fun `get=2次元要素取得`() {
        val d2 = IOType.d2(listOf(2, 2)) { x, y -> x + y * 2.0f }
        assertEquals(expected = 0.0f, actual = d2[0, 0].unwrap())
        assertEquals(expected = 2.0f, actual = d2[0, 1].unwrap())
        assertEquals(expected = 1.0f, actual = d2[1, 0].unwrap())
        assertEquals(expected = 3.0f, actual = d2[1, 1].unwrap())
    }

    @Test
    fun `get=2次元行取得`() {
        val d2 = IOType.d2(listOf(2, 2)) { x, y -> x + y * 2.0f }
        assertContentEquals(expected = IOType.d1(value = listOf(0.0f, 2.0f)), actual = d2[0])
        assertContentEquals(expected = IOType.d1(value = listOf(1.0f, 3.0f)), actual = d2[1])
    }

    @Test
    fun `i_j=D2のij次元`() {
        val d2 = IOType.d2(listOf(3, 4)) { _, _ -> 0.0f }
        assertEquals(expected = 3, actual = d2.i)
        assertEquals(expected = 4, actual = d2.j)
    }

    @Test
    fun `shape=D2の形状`() {
        val d2 = IOType.d2(listOf(2, 3)) { _, _ -> 0.0f }
        assertEquals(expected = listOf(2, 3), actual = d2.shape)
    }

    @Test
    fun `size=D2のサイズ`() {
        val d2 = IOType.d2(listOf(2, 3)) { _, _ -> 0.0f }
        assertEquals(expected = 6, actual = d2.size)
    }

    @Test
    fun `unwrap=D2の全要素を平坦なFloatArrayで取得`() {
        val d2 = IOType.d2(listOf(2, 3)) { i, j -> i * 3.0f + j }
        assertEquals(expected = listOf(0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f), actual = d2.unwrap().toList())
    }
}
