@file:Suppress("NonAsciiCharacters")

package com.wsr.operator

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class PlusExtTest {
    @Test
    fun `D1+D1=対応するindexの値を足したD1`() {
        val a = IOType.d1(listOf(1.0f, 2.0f, 3.0f))
        val b = IOType.d1(listOf(2.0f, 3.0f, 4.0f))
        assertEquals(
            expected = IOType.d1(listOf(3.0f, 5.0f, 7.0f)),
            actual = a + b,
        )
    }

    @Test
    fun `List_D1_+D1=各要素にD1を足したList_D1_`() {
        val list =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
                IOType.d1(listOf(4.0f, 5.0f, 6.0f)),
            )
        val b = IOType.d1(listOf(1.0f, 1.0f, 1.0f))
        val result = list + b
        assertEquals(
            expected = IOType.d1(listOf(2.0f, 3.0f, 4.0f)),
            actual = result[0],
        )
        assertEquals(
            expected = IOType.d1(listOf(5.0f, 6.0f, 7.0f)),
            actual = result[1],
        )
    }

    @Test
    fun `D2+D2=対応するindexの値を足したD2`() {
        val a = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        val b = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        val result = a + b
        assertEquals(
            expected = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() * 2 },
            actual = result,
        )
    }

    @Test
    fun `List_D2_+D2=各要素にD2を足したList_D2_`() {
        val list =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        val b = IOType.d2(2, 2) { _, _ -> 1.0f }
        val result = list + b
        assertEquals(
            expected = IOType.d2(2, 2) { x, y -> (x * 2 + y + 2).toFloat() },
            actual = result[0],
        )
        assertEquals(
            expected = IOType.d2(2, 2) { x, y -> (x * 2 + y + 6).toFloat() },
            actual = result[1],
        )
    }

    @Test
    fun `D3+D3=対応するindexの値を足したD3`() {
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val b = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a + b
        assertEquals(
            expected = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() * 2 },
            actual = result,
        )
    }

    @Test
    fun `List_D3_+D3=各要素にD3を足したList_D3_`() {
        val list =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 10).toFloat() },
            )
        val b = IOType.d3(2, 2, 2) { _, _, _ -> 1.0f }
        val result = list + b
        assertEquals(
            expected = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 2).toFloat() },
            actual = result[0],
        )
        assertEquals(
            expected = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 11).toFloat() },
            actual = result[1],
        )
    }
}
