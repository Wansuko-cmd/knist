@file:Suppress("NonAsciiCharacters")

package com.wsr.collection

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class BatchAverageExtTest {
    @Test
    fun `List_D1のbatchAverage=要素ごとの平均`() {
        val input =
            listOf(
                // [1, 2, 3]
                IOType.Companion.d1(listOf(1.0f, 2.0f, 3.0f)),
                // [4, 5, 6]
                IOType.Companion.d1(listOf(4.0f, 5.0f, 6.0f)),
            )
        // [(1+4)/2, (2+5)/2, (3+6)/2] = [2.5f, 3.5f, 4.5f]
        val result = input.batchAverage()
        assertEquals(expected = IOType.Companion.d1(listOf(2.5f, 3.5f, 4.5f)), actual = result)
    }

    @Test
    fun `List_D2のbatchAverage=要素ごとの平均`() {
        val input =
            listOf(
                // [[1, 2],
                //  [3, 4]]
                IOType.Companion.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
                // [[5, 6],
                //  [7, 8]]
                IOType.Companion.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        // [[(1+5)/2, (2+6)/2],
        //  [(3+7)/2, (4+8)/2]] = [[3.0f, 4.0f], [5.0f, 6.0f]]
        val result = input.batchAverage()
        assertEquals(
            expected = IOType.Companion.d2(
                2,
                2,
            ) { x, y -> ((x * 2 + y + 1) + (x * 2 + y + 5)) / 2.0f },
            actual = result,
        )
    }

    @Test
    fun `List_D3のbatchAverage=要素ごとの平均`() {
        val input =
            listOf(
                // [[[1, 2], [3, 4]],
                //  [[5, 6], [7, 8]]]
                IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
                // [[[9, 10], [11, 12]],
                //  [[13, 14], [15, 16]]]
                IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 9).toFloat() },
            )
        // [[(1+9)/2, (2+10)/2], [(3+11)/2, (4+12)/2]],
        //  [[(5+13)/2, (6+14)/2], [(7+15)/2, (8+16)/2]]]
        val result = input.batchAverage()
        assertEquals(expected = 5.0f, actual = result[0, 0, 0])
        assertEquals(expected = 6.0f, actual = result[0, 0, 1])
        assertEquals(expected = 7.0f, actual = result[0, 1, 0])
        assertEquals(expected = 8.0f, actual = result[0, 1, 1])
        assertEquals(expected = 9.0f, actual = result[1, 0, 0])
        assertEquals(expected = 10.0f, actual = result[1, 0, 1])
        assertEquals(expected = 11.0f, actual = result[1, 1, 0])
        assertEquals(expected = 12.0f, actual = result[1, 1, 1])
    }
}
