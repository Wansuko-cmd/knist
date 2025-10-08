@file:Suppress("NonAsciiCharacters")

package com.wsr.operator

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class MinusExtTest {
    @Test
    fun `D1-D1=対応するindexの値を引いたD1`() {
        val a = IOType.d1(listOf(5.0, 7.0, 9.0))
        val b = IOType.d1(listOf(2.0, 3.0, 4.0))
        assertEquals(
            expected = IOType.d1(listOf(3.0, 4.0, 5.0)),
            actual = a - b,
        )
    }

    @Test
    fun `List_D1-D1=各要素からD1を引いたList_D1`() {
        val list = listOf(
            IOType.d1(listOf(5.0, 6.0, 7.0)),
            IOType.d1(listOf(8.0, 9.0, 10.0)),
        )
        val b = IOType.d1(listOf(1.0, 1.0, 1.0))
        val result = list - b
        assertEquals(
            expected = IOType.d1(listOf(4.0, 5.0, 6.0)),
            actual = result[0],
        )
        assertEquals(
            expected = IOType.d1(listOf(7.0, 8.0, 9.0)),
            actual = result[1],
        )
    }

    @Test
    fun `D2-D2=対応するindexの値を引いたD2`() {
        val a = IOType.d2(2, 3) { x, y -> (x * 3 + y + 5).toDouble() }
        val b = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toDouble() }
        val result = a - b
        assertEquals(
            expected = IOType.d2(2, 3) { _, _ -> 4.0 },
            actual = result,
        )
    }

    @Test
    fun `List_D2-D2=各要素からD2を引いたList_D2`() {
        val list = listOf(
            IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toDouble() },
            IOType.d2(2, 2) { x, y -> (x * 2 + y + 10).toDouble() },
        )
        val b = IOType.d2(2, 2) { _, _ -> 1.0 }
        val result = list - b
        assertEquals(
            expected = IOType.d2(2, 2) { x, y -> (x * 2 + y + 4).toDouble() },
            actual = result[0],
        )
        assertEquals(
            expected = IOType.d2(2, 2) { x, y -> (x * 2 + y + 9).toDouble() },
            actual = result[1],
        )
    }

    @Test
    fun `D3-D3=対応するindexの値を引いたD3`() {
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 10).toDouble() }
        val b = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() }
        val result = a - b
        assertEquals(
            expected = IOType.d3(2, 2, 2) { _, _, _ -> 9.0 },
            actual = result,
        )
    }

    @Test
    fun `List_D3-D3=各要素からD3を引いたList_D3`() {
        val list = listOf(
            IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 10).toDouble() },
            IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 20).toDouble() },
        )
        val b = IOType.d3(2, 2, 2) { _, _, _ -> 1.0 }
        val result = list - b
        assertEquals(
            expected = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 9).toDouble() },
            actual = result[0],
        )
        assertEquals(
            expected = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 19).toDouble() },
            actual = result[1],
        )
    }
}
