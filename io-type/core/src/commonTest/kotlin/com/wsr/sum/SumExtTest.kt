@file:Suppress("NonAsciiCharacters")

package com.wsr.sum

import com.wsr.IOType
import com.wsr.collection.sum
import kotlin.test.Test
import kotlin.test.assertEquals

class SumExtTest {
    @Test
    fun `D1のsum=全要素の合計`() {
        val a = IOType.d1(listOf(1.0, 2.0, 3.0, 4.0))
        val result = a.sum()
        assertEquals(
            expected = 10.0,
            actual = result,
        )
    }

    @Test
    fun `List_D1のsum=各インデックスごとの合計を持つD1`() {
        val list =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
                IOType.d1(listOf(4.0, 5.0, 6.0)),
                IOType.d1(listOf(7.0, 8.0, 9.0)),
            )
        val result = list.sum()
        assertEquals(
            expected = IOType.d1(listOf(12.0, 15.0, 18.0)),
            actual = result,
        )
    }

    @Test
    fun `D2のsum=全要素の合計`() {
        val a = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toDouble() }
        val result = a.sum()
        assertEquals(
            expected = 21.0,
            actual = result,
        )
    }

    @Test
    fun `D2のsum_axis0=各列の合計`() {
        // [[1, 2],
        //  [3, 4],
        //  [5, 6]]
        val a = IOType.d2(3, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val result = a.sum(axis = 0)
        assertEquals(
            expected = IOType.d1(listOf(9.0, 12.0)),
            actual = result,
        )
    }

    @Test
    fun `D2のsum_axis1=各行の合計`() {
        // [[1, 2],
        //  [3, 4],
        //  [5, 6]]
        val a = IOType.d2(3, 2) { x, y -> (x * 2 + y + 1).toDouble() }
        val result = a.sum(axis = 1)
        assertEquals(
            expected = IOType.d1(listOf(3.0, 7.0, 11.0)),
            actual = result,
        )
    }
}
