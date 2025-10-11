@file:Suppress("NonAsciiCharacters")

package com.wsr

import kotlin.test.Test
import kotlin.test.assertEquals

class D4Test {
    @Test
    fun `D4のget_ijkl=各要素取得`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0 + k * 4.0 + l * 8.0 }
        assertEquals(expected = 0.0, actual = d4[0, 0, 0, 0])
        assertEquals(expected = 8.0, actual = d4[0, 0, 0, 1])
        assertEquals(expected = 4.0, actual = d4[0, 0, 1, 0])
        assertEquals(expected = 12.0, actual = d4[0, 0, 1, 1])
        assertEquals(expected = 2.0, actual = d4[0, 1, 0, 0])
        assertEquals(expected = 10.0, actual = d4[0, 1, 0, 1])
        assertEquals(expected = 6.0, actual = d4[0, 1, 1, 0])
        assertEquals(expected = 14.0, actual = d4[0, 1, 1, 1])
        assertEquals(expected = 1.0, actual = d4[1, 0, 0, 0])
        assertEquals(expected = 9.0, actual = d4[1, 0, 0, 1])
        assertEquals(expected = 5.0, actual = d4[1, 0, 1, 0])
        assertEquals(expected = 13.0, actual = d4[1, 0, 1, 1])
        assertEquals(expected = 3.0, actual = d4[1, 1, 0, 0])
        assertEquals(expected = 11.0, actual = d4[1, 1, 0, 1])
        assertEquals(expected = 7.0, actual = d4[1, 1, 1, 0])
        assertEquals(expected = 15.0, actual = d4[1, 1, 1, 1])
    }

    @Test
    fun `D4のget_ijk=各D1取得`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0 + k * 4.0 + l * 8.0 }
        assertEquals(expected = IOType.d1(listOf(0.0, 8.0)), actual = d4[0, 0, 0])
        assertEquals(expected = IOType.d1(listOf(4.0, 12.0)), actual = d4[0, 0, 1])
        assertEquals(expected = IOType.d1(listOf(2.0, 10.0)), actual = d4[0, 1, 0])
        assertEquals(expected = IOType.d1(listOf(6.0, 14.0)), actual = d4[0, 1, 1])
        assertEquals(expected = IOType.d1(listOf(1.0, 9.0)), actual = d4[1, 0, 0])
        assertEquals(expected = IOType.d1(listOf(5.0, 13.0)), actual = d4[1, 0, 1])
        assertEquals(expected = IOType.d1(listOf(3.0, 11.0)), actual = d4[1, 1, 0])
        assertEquals(expected = IOType.d1(listOf(7.0, 15.0)), actual = d4[1, 1, 1])
    }

    @Test
    fun `D4のget_ij=各D2取得`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0 + k * 4.0 + l * 8.0 }
        assertEquals(
            expected = IOType.d2(
                shape = listOf(2, 2),
                value = listOf(0.0, 8.0, 4.0, 12.0),
            ),
            actual = d4[0, 0],
        )
        assertEquals(
            expected = IOType.d2(
                shape = listOf(2, 2),
                value = listOf(2.0, 10.0, 6.0, 14.0),
            ),
            actual = d4[0, 1],
        )
        assertEquals(
            expected = IOType.d2(
                shape = listOf(2, 2),
                value = listOf(1.0, 9.0, 5.0, 13.0),
            ),
            actual = d4[1, 0],
        )
        assertEquals(
            expected = IOType.d2(
                shape = listOf(2, 2),
                value = listOf(3.0, 11.0, 7.0, 15.0),
            ),
            actual = d4[1, 1],
        )
    }

    @Test
    fun `D4のget_i=各D3取得`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0 + k * 4.0 + l * 8.0 }
        assertEquals(
            expected = IOType.d3(
                shape = listOf(2, 2, 2),
                value = listOf(0.0, 8.0, 4.0, 12.0, 2.0, 10.0, 6.0, 14.0),
            ),
            actual = d4[0],
        )
        assertEquals(
            expected = IOType.d3(
                shape = listOf(2, 2, 2),
                value = listOf(1.0, 9.0, 5.0, 13.0, 3.0, 11.0, 7.0, 15.0),
            ),
            actual = d4[1],
        )
    }

    @Test
    fun `D4のset=各要素設定`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0 + k * 4.0 + l * 8.0 }
        d4[0, 0, 0, 0] = 15.0
        d4[0, 0, 0, 1] = 7.0
        d4[0, 0, 1, 0] = 11.0
        d4[0, 0, 1, 1] = 3.0
        d4[0, 1, 0, 0] = 13.0
        d4[0, 1, 0, 1] = 5.0
        d4[0, 1, 1, 0] = 9.0
        d4[0, 1, 1, 1] = 1.0
        d4[1, 0, 0, 0] = 14.0
        d4[1, 0, 0, 1] = 6.0
        d4[1, 0, 1, 0] = 10.0
        d4[1, 0, 1, 1] = 2.0
        d4[1, 1, 0, 0] = 12.0
        d4[1, 1, 0, 1] = 4.0
        d4[1, 1, 1, 0] = 8.0
        d4[1, 1, 1, 1] = 0.0

        assertEquals(expected = 15.0, actual = d4[0, 0, 0, 0])
        assertEquals(expected = 7.0, actual = d4[0, 0, 0, 1])
        assertEquals(expected = 11.0, actual = d4[0, 0, 1, 0])
        assertEquals(expected = 3.0, actual = d4[0, 0, 1, 1])
        assertEquals(expected = 13.0, actual = d4[0, 1, 0, 0])
        assertEquals(expected = 5.0, actual = d4[0, 1, 0, 1])
        assertEquals(expected = 9.0, actual = d4[0, 1, 1, 0])
        assertEquals(expected = 1.0, actual = d4[0, 1, 1, 1])
        assertEquals(expected = 14.0, actual = d4[1, 0, 0, 0])
        assertEquals(expected = 6.0, actual = d4[1, 0, 0, 1])
        assertEquals(expected = 10.0, actual = d4[1, 0, 1, 0])
        assertEquals(expected = 2.0, actual = d4[1, 0, 1, 1])
        assertEquals(expected = 12.0, actual = d4[1, 1, 0, 0])
        assertEquals(expected = 4.0, actual = d4[1, 1, 0, 1])
        assertEquals(expected = 8.0, actual = d4[1, 1, 1, 0])
        assertEquals(expected = 0.0, actual = d4[1, 1, 1, 1])
    }
}
