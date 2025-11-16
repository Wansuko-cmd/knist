@file:Suppress("NonAsciiCharacters")

package com.wsr

import kotlin.test.Test
import kotlin.test.assertEquals

class D4Test {
    @Test
    fun `D4のget_ijkl=各要素取得`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0f + k * 4.0f + l * 8.0f }
        assertEquals(expected = 0.0f, actual = d4[0, 0, 0, 0])
        assertEquals(expected = 8.0f, actual = d4[0, 0, 0, 1])
        assertEquals(expected = 4.0f, actual = d4[0, 0, 1, 0])
        assertEquals(expected = 12.0f, actual = d4[0, 0, 1, 1])
        assertEquals(expected = 2.0f, actual = d4[0, 1, 0, 0])
        assertEquals(expected = 10.0f, actual = d4[0, 1, 0, 1])
        assertEquals(expected = 6.0f, actual = d4[0, 1, 1, 0])
        assertEquals(expected = 14.0f, actual = d4[0, 1, 1, 1])
        assertEquals(expected = 1.0f, actual = d4[1, 0, 0, 0])
        assertEquals(expected = 9.0f, actual = d4[1, 0, 0, 1])
        assertEquals(expected = 5.0f, actual = d4[1, 0, 1, 0])
        assertEquals(expected = 13.0f, actual = d4[1, 0, 1, 1])
        assertEquals(expected = 3.0f, actual = d4[1, 1, 0, 0])
        assertEquals(expected = 11.0f, actual = d4[1, 1, 0, 1])
        assertEquals(expected = 7.0f, actual = d4[1, 1, 1, 0])
        assertEquals(expected = 15.0f, actual = d4[1, 1, 1, 1])
    }

    @Test
    fun `D4のget_ijk=各D1取得`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0f + k * 4.0f + l * 8.0f }
        assertEquals(expected = IOType.d1(listOf(0.0f, 8.0f)), actual = d4[0, 0, 0])
        assertEquals(expected = IOType.d1(listOf(4.0f, 12.0f)), actual = d4[0, 0, 1])
        assertEquals(expected = IOType.d1(listOf(2.0f, 10.0f)), actual = d4[0, 1, 0])
        assertEquals(expected = IOType.d1(listOf(6.0f, 14.0f)), actual = d4[0, 1, 1])
        assertEquals(expected = IOType.d1(listOf(1.0f, 9.0f)), actual = d4[1, 0, 0])
        assertEquals(expected = IOType.d1(listOf(5.0f, 13.0f)), actual = d4[1, 0, 1])
        assertEquals(expected = IOType.d1(listOf(3.0f, 11.0f)), actual = d4[1, 1, 0])
        assertEquals(expected = IOType.d1(listOf(7.0f, 15.0f)), actual = d4[1, 1, 1])
    }

    @Test
    fun `D4のget_ij=各D2取得`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0f + k * 4.0f + l * 8.0f }
        assertEquals(
            expected = IOType.d2(
                shape = listOf(2, 2),
                value = listOf(0.0f, 8.0f, 4.0f, 12.0f),
            ),
            actual = d4[0, 0],
        )
        assertEquals(
            expected = IOType.d2(
                shape = listOf(2, 2),
                value = listOf(2.0f, 10.0f, 6.0f, 14.0f),
            ),
            actual = d4[0, 1],
        )
        assertEquals(
            expected = IOType.d2(
                shape = listOf(2, 2),
                value = listOf(1.0f, 9.0f, 5.0f, 13.0f),
            ),
            actual = d4[1, 0],
        )
        assertEquals(
            expected = IOType.d2(
                shape = listOf(2, 2),
                value = listOf(3.0f, 11.0f, 7.0f, 15.0f),
            ),
            actual = d4[1, 1],
        )
    }

    @Test
    fun `D4のget_i=各D3取得`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0f + k * 4.0f + l * 8.0f }
        assertEquals(
            expected = IOType.d3(
                shape = listOf(2, 2, 2),
                value = listOf(0.0f, 8.0f, 4.0f, 12.0f, 2.0f, 10.0f, 6.0f, 14.0f),
            ),
            actual = d4[0],
        )
        assertEquals(
            expected = IOType.d3(
                shape = listOf(2, 2, 2),
                value = listOf(1.0f, 9.0f, 5.0f, 13.0f, 3.0f, 11.0f, 7.0f, 15.0f),
            ),
            actual = d4[1],
        )
    }

    @Test
    fun `D4のset=各要素設定`() {
        val d4 = IOType.d4(listOf(2, 2, 2, 2)) { i, j, k, l -> i + j * 2.0f + k * 4.0f + l * 8.0f }
        d4[0, 0, 0, 0] = 15.0f
        d4[0, 0, 0, 1] = 7.0f
        d4[0, 0, 1, 0] = 11.0f
        d4[0, 0, 1, 1] = 3.0f
        d4[0, 1, 0, 0] = 13.0f
        d4[0, 1, 0, 1] = 5.0f
        d4[0, 1, 1, 0] = 9.0f
        d4[0, 1, 1, 1] = 1.0f
        d4[1, 0, 0, 0] = 14.0f
        d4[1, 0, 0, 1] = 6.0f
        d4[1, 0, 1, 0] = 10.0f
        d4[1, 0, 1, 1] = 2.0f
        d4[1, 1, 0, 0] = 12.0f
        d4[1, 1, 0, 1] = 4.0f
        d4[1, 1, 1, 0] = 8.0f
        d4[1, 1, 1, 1] = 0.0f

        assertEquals(expected = 15.0f, actual = d4[0, 0, 0, 0])
        assertEquals(expected = 7.0f, actual = d4[0, 0, 0, 1])
        assertEquals(expected = 11.0f, actual = d4[0, 0, 1, 0])
        assertEquals(expected = 3.0f, actual = d4[0, 0, 1, 1])
        assertEquals(expected = 13.0f, actual = d4[0, 1, 0, 0])
        assertEquals(expected = 5.0f, actual = d4[0, 1, 0, 1])
        assertEquals(expected = 9.0f, actual = d4[0, 1, 1, 0])
        assertEquals(expected = 1.0f, actual = d4[0, 1, 1, 1])
        assertEquals(expected = 14.0f, actual = d4[1, 0, 0, 0])
        assertEquals(expected = 6.0f, actual = d4[1, 0, 0, 1])
        assertEquals(expected = 10.0f, actual = d4[1, 0, 1, 0])
        assertEquals(expected = 2.0f, actual = d4[1, 0, 1, 1])
        assertEquals(expected = 12.0f, actual = d4[1, 1, 0, 0])
        assertEquals(expected = 4.0f, actual = d4[1, 1, 0, 1])
        assertEquals(expected = 8.0f, actual = d4[1, 1, 1, 0])
        assertEquals(expected = 0.0f, actual = d4[1, 1, 1, 1])
    }
}
