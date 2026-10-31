@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.core

import com.wsr.knist.assertContentEquals
import kotlin.test.Test
import kotlin.test.assertEquals

class D3Test {
    @Test
    fun `get=3次元要素取得`() {
        val d3 = IOType.d3(listOf(2, 2, 2)) { x, y, z -> x + y * 2.0f + z * 4.0f }
        assertEquals(expected = 0.0f, actual = d3[0, 0, 0].unwrap())
        assertEquals(expected = 4.0f, actual = d3[0, 0, 1].unwrap())
        assertEquals(expected = 2.0f, actual = d3[0, 1, 0].unwrap())
        assertEquals(expected = 6.0f, actual = d3[0, 1, 1].unwrap())
        assertEquals(expected = 1.0f, actual = d3[1, 0, 0].unwrap())
        assertEquals(expected = 5.0f, actual = d3[1, 0, 1].unwrap())
        assertEquals(expected = 3.0f, actual = d3[1, 1, 0].unwrap())
        assertEquals(expected = 7.0f, actual = d3[1, 1, 1].unwrap())
    }

    @Test
    fun `get=3次元行取得`() {
        val d3 = IOType.d3(listOf(2, 2, 2)) { x, y, z -> x + y * 2.0f + z * 4.0f }
        assertContentEquals(expected = IOType.d1(value = listOf(0.0f, 4.0f)), actual = d3[0, 0])
        assertContentEquals(expected = IOType.d1(value = listOf(2.0f, 6.0f)), actual = d3[0, 1])
        assertContentEquals(expected = IOType.d1(value = listOf(1.0f, 5.0f)), actual = d3[1, 0])
        assertContentEquals(expected = IOType.d1(value = listOf(3.0f, 7.0f)), actual = d3[1, 1])
    }

    @Test
    fun `get=3次元面取得`() {
        val d3 = IOType.d3(listOf(2, 2, 2)) { x, y, z -> x + y * 2.0f + z * 4.0f }
        assertContentEquals(
            expected = IOType.d2(shape = listOf(2, 2), value = listOf(0.0f, 4.0f, 2.0f, 6.0f)),
            actual = d3[0],
        )
        assertContentEquals(
            expected = IOType.d2(shape = listOf(2, 2), value = listOf(1.0f, 5.0f, 3.0f, 7.0f)),
            actual = d3[1],
        )
    }

    @Test
    fun `i_j_k=D3のijk次元`() {
        val d3 = IOType.d3(listOf(2, 3, 4)) { _, _, _ -> 0.0f }
        assertEquals(expected = 2, actual = d3.i)
        assertEquals(expected = 3, actual = d3.j)
        assertEquals(expected = 4, actual = d3.k)
    }

    @Test
    fun `shape=D3の形状`() {
        val d3 = IOType.d3(listOf(2, 3, 4)) { _, _, _ -> 0.0f }
        assertEquals(expected = listOf(2, 3, 4), actual = d3.shape)
    }

    @Test
    fun `size=D3のサイズ`() {
        val d3 = IOType.d3(listOf(2, 3, 4)) { _, _, _ -> 0.0f }
        assertEquals(expected = 24, actual = d3.size)
    }
}
