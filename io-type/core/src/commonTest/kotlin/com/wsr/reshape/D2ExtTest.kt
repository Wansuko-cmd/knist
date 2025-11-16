@file:Suppress("NonAsciiCharacters")

package com.wsr.reshape

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class D2ExtTest {
    @Test
    fun `List_D2のtoD3=D2をまとめたD3`() {
        val list =
            listOf(
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() },
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 7).toFloat() },
            )
        val result = list.toD3()
        assertEquals(
            expected = IOType.d3(2, 2, 3) { x, y, z -> (y * 3 + z + 1 + x * 6).toFloat() },
            actual = result,
        )
    }

    @Test
    fun `D2のtranspose=行と列を入れ替えたD2`() {
        // [[1, 2],
        //  [3, 4],
        //  [5, 6]]
        val a = IOType.d2(3, 2) { x, y -> (x * 2 + y + 1).toFloat() }

        // [[1, 3, 5],
        //  [2, 4, 6]]
        val result = a.transpose()
        assertEquals(
            expected = IOType.d2(2, 3) { x, y -> (y * 2 + x + 1).toFloat() },
            actual = result,
        )
    }

    @Test
    fun `List_D2のtranspose=各D2をtransposeしたList_D2`() {
        val list =
            listOf(
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() },
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 7).toFloat() },
            )
        val result = list.transpose()
        assertEquals(
            expected = IOType.d2(3, 2) { x, y -> (y * 3 + x + 1).toFloat() },
            actual = result[0],
        )
        assertEquals(
            expected = IOType.d2(3, 2) { x, y -> (y * 3 + x + 7).toFloat() },
            actual = result[1],
        )
    }
}
