@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.core

import com.wsr.knist.assertContentEquals
import kotlin.test.Test
import kotlin.test.assertEquals

class D4Test {
    @Test
    fun `get=4次元要素取得`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0f + k * 4.0f + l * 8.0f }
        assertEquals(expected = 0.0f, actual = d4[0, 0, 0, 0].unwrap())
        assertEquals(expected = 8.0f, actual = d4[0, 0, 0, 1].unwrap())
        assertEquals(expected = 4.0f, actual = d4[0, 0, 1, 0].unwrap())
        assertEquals(expected = 12.0f, actual = d4[0, 0, 1, 1].unwrap())
        assertEquals(expected = 2.0f, actual = d4[0, 1, 0, 0].unwrap())
        assertEquals(expected = 10.0f, actual = d4[0, 1, 0, 1].unwrap())
        assertEquals(expected = 6.0f, actual = d4[0, 1, 1, 0].unwrap())
        assertEquals(expected = 14.0f, actual = d4[0, 1, 1, 1].unwrap())
        assertEquals(expected = 1.0f, actual = d4[1, 0, 0, 0].unwrap())
        assertEquals(expected = 9.0f, actual = d4[1, 0, 0, 1].unwrap())
        assertEquals(expected = 5.0f, actual = d4[1, 0, 1, 0].unwrap())
        assertEquals(expected = 13.0f, actual = d4[1, 0, 1, 1].unwrap())
        assertEquals(expected = 3.0f, actual = d4[1, 1, 0, 0].unwrap())
        assertEquals(expected = 11.0f, actual = d4[1, 1, 0, 1].unwrap())
        assertEquals(expected = 7.0f, actual = d4[1, 1, 1, 0].unwrap())
        assertEquals(expected = 15.0f, actual = d4[1, 1, 1, 1].unwrap())
    }

    @Test
    fun `get=4次元行取得`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0f + k * 4.0f + l * 8.0f }
        assertContentEquals(expected = IOType.d1(listOf(0.0f, 8.0f)), actual = d4[0, 0, 0])
        assertContentEquals(expected = IOType.d1(listOf(4.0f, 12.0f)), actual = d4[0, 0, 1])
        assertContentEquals(expected = IOType.d1(listOf(2.0f, 10.0f)), actual = d4[0, 1, 0])
        assertContentEquals(expected = IOType.d1(listOf(6.0f, 14.0f)), actual = d4[0, 1, 1])
        assertContentEquals(expected = IOType.d1(listOf(1.0f, 9.0f)), actual = d4[1, 0, 0])
        assertContentEquals(expected = IOType.d1(listOf(5.0f, 13.0f)), actual = d4[1, 0, 1])
        assertContentEquals(expected = IOType.d1(listOf(3.0f, 11.0f)), actual = d4[1, 1, 0])
        assertContentEquals(expected = IOType.d1(listOf(7.0f, 15.0f)), actual = d4[1, 1, 1])
    }

    @Test
    fun `get=4次元面取得`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0f + k * 4.0f + l * 8.0f }
        assertContentEquals(
            expected = IOType.d2(shape = listOf(2, 2), value = listOf(0.0f, 8.0f, 4.0f, 12.0f)),
            actual = d4[0, 0],
        )
        assertContentEquals(
            expected = IOType.d2(shape = listOf(2, 2), value = listOf(2.0f, 10.0f, 6.0f, 14.0f)),
            actual = d4[0, 1],
        )
        assertContentEquals(
            expected = IOType.d2(shape = listOf(2, 2), value = listOf(1.0f, 9.0f, 5.0f, 13.0f)),
            actual = d4[1, 0],
        )
        assertContentEquals(
            expected = IOType.d2(shape = listOf(2, 2), value = listOf(3.0f, 11.0f, 7.0f, 15.0f)),
            actual = d4[1, 1],
        )
    }

    @Test
    fun `get=4次元体取得`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0f + k * 4.0f + l * 8.0f }
        assertContentEquals(
            expected = IOType.d3(
                shape = listOf(2, 2, 2),
                value = listOf(0.0f, 8.0f, 4.0f, 12.0f, 2.0f, 10.0f, 6.0f, 14.0f),
            ),
            actual = d4[0],
        )
        assertContentEquals(
            expected = IOType.d3(
                shape = listOf(2, 2, 2),
                value = listOf(1.0f, 9.0f, 5.0f, 13.0f, 3.0f, 11.0f, 7.0f, 15.0f),
            ),
            actual = d4[1],
        )
    }

    @Test
    fun `i_j_k_l=D4のijkl次元`() {
        val d4 = IOType.d4(listOf(2, 3, 4, 5)) { _, _, _, _ -> 0.0f }
        assertEquals(expected = 2, actual = d4.i)
        assertEquals(expected = 3, actual = d4.j)
        assertEquals(expected = 4, actual = d4.k)
        assertEquals(expected = 5, actual = d4.l)
    }

    @Test
    fun `shape=D4の形状`() {
        val d4 = IOType.d4(listOf(2, 3, 4, 5)) { _, _, _, _ -> 0.0f }
        assertEquals(expected = listOf(2, 3, 4, 5), actual = d4.shape)
    }

    @Test
    fun `size=D4のサイズ`() {
        val d4 = IOType.d4(listOf(2, 3, 4, 5)) { _, _, _, _ -> 0.0f }
        assertEquals(expected = 120, actual = d4.size)
    }
}
