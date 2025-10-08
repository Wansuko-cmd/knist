@file:Suppress("NonAsciiCharacters")

package com.wsr.operator

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeExtTest {
    @Test
    fun `Double×D1=各要素にDoubleをかけたD1`() {
        val a = IOType.d1(listOf(1.0, 2.0, 3.0))
        val result = 2.0 * a
        assertEquals(
            expected = IOType.d1(listOf(2.0, 4.0, 6.0)),
            actual = result,
        )
    }

    @Test
    fun `Double×List_D1=各要素にDoubleをかけたList_D1`() {
        val list =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
                IOType.d1(listOf(4.0, 5.0, 6.0)),
            )
        val result = 2.0 * list
        assertEquals(
            expected = IOType.d1(listOf(2.0, 4.0, 6.0)),
            actual = result[0],
        )
        assertEquals(
            expected = IOType.d1(listOf(8.0, 10.0, 12.0)),
            actual = result[1],
        )
    }

    @Test
    fun `Double×D2=各要素にDoubleをかけたD2`() {
        val a = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toDouble() }
        val result = 2.0 * a
        assertEquals(
            expected = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toDouble() * 2 },
            actual = result,
        )
    }

    @Test
    fun `Double×List_D2=各要素にDoubleをかけたList_D2`() {
        val list =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toDouble() },
            )
        val result = 3.0 * list
        assertEquals(
            expected = IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() * 3 },
            actual = result[0],
        )
        assertEquals(
            expected = IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toDouble() * 3 },
            actual = result[1],
        )
    }

    @Test
    fun `Double×D3=各要素にDoubleをかけたD3`() {
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() }
        val result = 2.0 * a
        assertEquals(
            expected = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() * 2 },
            actual = result,
        )
    }
}
