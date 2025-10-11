@file:Suppress("NonAsciiCharacters")

package com.wsr

import kotlin.test.Test
import kotlin.test.assertEquals

class D3Test {
    @Test
    fun `D3のget_ijk=各要素取得`() {
        val d3 = IOType.d3(listOf(2, 2, 2)) { x, y, z -> x + y * 2.0 + z * 4.0 }
        assertEquals(expected = 0.0, actual = d3[0, 0, 0])
        assertEquals(expected = 4.0, actual = d3[0, 0, 1])
        assertEquals(expected = 2.0, actual = d3[0, 1, 0])
        assertEquals(expected = 6.0, actual = d3[0, 1, 1])
        assertEquals(expected = 1.0, actual = d3[1, 0, 0])
        assertEquals(expected = 5.0, actual = d3[1, 0, 1])
        assertEquals(expected = 3.0, actual = d3[1, 1, 0])
        assertEquals(expected = 7.0, actual = d3[1, 1, 1])
    }

    @Test
    fun `D3のget_ij=各D1取得`() {
        val d3 = IOType.d3(listOf(2, 2, 2)) { x, y, z -> x + y * 2.0 + z * 4.0 }
        assertEquals(expected = IOType.d1(value = listOf(0.0, 4.0)), actual = d3[0, 0])
        assertEquals(expected = IOType.d1(value = listOf(2.0, 6.0)), actual = d3[0, 1])
        assertEquals(expected = IOType.d1(value = listOf(1.0, 5.0)), actual = d3[1, 0])
        assertEquals(expected = IOType.d1(value = listOf(3.0, 7.0)), actual = d3[1, 1])
    }

    @Test
    fun `D3のget_i=各D2取得`() {
        val d3 = IOType.d3(listOf(2, 2, 2)) { x, y, z -> x + y * 2.0 + z * 4.0 }
        assertEquals(expected = IOType.d2(shape = listOf(2, 2), value = listOf(0.0, 4.0, 2.0, 6.0)), actual = d3[0])
        assertEquals(expected = IOType.d2(shape = listOf(2, 2), value = listOf(1.0, 5.0, 3.0, 7.0)), actual = d3[1])
    }

    @Test
    fun `D3のset=各要素設定`() {
        val d3 = IOType.d3(listOf(2, 2, 2)) { x, y, z -> x + y * 2.0 + z * 4.0 }
        d3[0, 0, 0] = 7.0
        d3[0, 0, 1] = 3.0
        d3[0, 1, 0] = 5.0
        d3[0, 1, 1] = 1.0
        d3[1, 0, 0] = 6.0
        d3[1, 0, 1] = 2.0
        d3[1, 1, 0] = 4.0
        d3[1, 1, 1] = 0.0
        assertEquals(expected = 7.0, actual = d3[0, 0, 0])
        assertEquals(expected = 3.0, actual = d3[0, 0, 1])
        assertEquals(expected = 5.0, actual = d3[0, 1, 0])
        assertEquals(expected = 1.0, actual = d3[0, 1, 1])
        assertEquals(expected = 6.0, actual = d3[1, 0, 0])
        assertEquals(expected = 2.0, actual = d3[1, 0, 1])
        assertEquals(expected = 4.0, actual = d3[1, 1, 0])
        assertEquals(expected = 0.0, actual = d3[1, 1, 1])
    }
}
