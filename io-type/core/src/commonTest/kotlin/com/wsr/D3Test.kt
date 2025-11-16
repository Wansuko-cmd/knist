@file:Suppress("NonAsciiCharacters")

package com.wsr

import kotlin.test.Test
import kotlin.test.assertEquals

class D3Test {
    @Test
    fun `D3のget_ijk=各要素取得`() {
        val d3 = IOType.d3(listOf(2, 2, 2)) { x, y, z -> x + y * 2.0f + z * 4.0f }
        assertEquals(expected = 0.0f, actual = d3[0, 0, 0])
        assertEquals(expected = 4.0f, actual = d3[0, 0, 1])
        assertEquals(expected = 2.0f, actual = d3[0, 1, 0])
        assertEquals(expected = 6.0f, actual = d3[0, 1, 1])
        assertEquals(expected = 1.0f, actual = d3[1, 0, 0])
        assertEquals(expected = 5.0f, actual = d3[1, 0, 1])
        assertEquals(expected = 3.0f, actual = d3[1, 1, 0])
        assertEquals(expected = 7.0f, actual = d3[1, 1, 1])
    }

    @Test
    fun `D3のget_ij=各D1取得`() {
        val d3 = IOType.d3(listOf(2, 2, 2)) { x, y, z -> x + y * 2.0f + z * 4.0f }
        assertEquals(expected = IOType.d1(value = listOf(0.0f, 4.0f)), actual = d3[0, 0])
        assertEquals(expected = IOType.d1(value = listOf(2.0f, 6.0f)), actual = d3[0, 1])
        assertEquals(expected = IOType.d1(value = listOf(1.0f, 5.0f)), actual = d3[1, 0])
        assertEquals(expected = IOType.d1(value = listOf(3.0f, 7.0f)), actual = d3[1, 1])
    }

    @Test
    fun `D3のget_i=各D2取得`() {
        val d3 = IOType.d3(listOf(2, 2, 2)) { x, y, z -> x + y * 2.0f + z * 4.0f }
        assertEquals(expected = IOType.d2(shape = listOf(2, 2), value = listOf(0.0f, 4.0f, 2.0f, 6.0f)), actual = d3[0])
        assertEquals(expected = IOType.d2(shape = listOf(2, 2), value = listOf(1.0f, 5.0f, 3.0f, 7.0f)), actual = d3[1])
    }

    @Test
    fun `D3のset=各要素設定`() {
        val d3 = IOType.d3(listOf(2, 2, 2)) { x, y, z -> x + y * 2.0f + z * 4.0f }
        d3[0, 0, 0] = 7.0f
        d3[0, 0, 1] = 3.0f
        d3[0, 1, 0] = 5.0f
        d3[0, 1, 1] = 1.0f
        d3[1, 0, 0] = 6.0f
        d3[1, 0, 1] = 2.0f
        d3[1, 1, 0] = 4.0f
        d3[1, 1, 1] = 0.0f
        assertEquals(expected = 7.0f, actual = d3[0, 0, 0])
        assertEquals(expected = 3.0f, actual = d3[0, 0, 1])
        assertEquals(expected = 5.0f, actual = d3[0, 1, 0])
        assertEquals(expected = 1.0f, actual = d3[0, 1, 1])
        assertEquals(expected = 6.0f, actual = d3[1, 0, 0])
        assertEquals(expected = 2.0f, actual = d3[1, 0, 1])
        assertEquals(expected = 4.0f, actual = d3[1, 1, 0])
        assertEquals(expected = 0.0f, actual = d3[1, 1, 1])
    }
}
