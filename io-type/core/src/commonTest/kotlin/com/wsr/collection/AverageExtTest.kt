@file:Suppress("NonAsciiCharacters")

package com.wsr.collection

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class AverageExtTest {
    @Test
    fun `D1のaverage=平均値`() {
        // [1, 2, 3, 4, 5]
        val input = IOType.Companion.d1(listOf(1.0, 2.0, 3.0, 4.0, 5.0))
        // (1 + 2 + 3 + 4 + 5) / 5 = 3.0
        val result = input.average()
        assertEquals(expected = 3.0, actual = result)
    }

    @Test
    fun `List_D1のaverage=要素ごとの平均`() {
        val input =
            listOf(
                // [1, 2, 3]
                IOType.Companion.d1(listOf(1.0, 2.0, 3.0)),
                // [4, 5, 6]
                IOType.Companion.d1(listOf(4.0, 5.0, 6.0)),
            )
        // [(1+4)/2, (2+5)/2, (3+6)/2] = [2.5, 3.5, 4.5]
        val result = input.average()
        assertEquals(expected = IOType.Companion.d1(listOf(2.5, 3.5, 4.5)), actual = result)
    }

    @Test
    fun `D2のaverage=各行の平均`() {
        // [[1, 2, 3],
        //  [4, 5, 6]]
        val input = IOType.Companion.d2(2, 3) { x, y -> (x * 3 + y + 1).toDouble() }
        // [(1+2+3)/3, (4+5+6)/3] = [2.0, 5.0]
        val result = input.average()
        assertEquals(expected = IOType.Companion.d1(listOf(2.0, 5.0)), actual = result)
    }

    @Test
    fun `List_D2のaverage=要素ごとの平均`() {
        val input =
            listOf(
                // [[1, 2],
                //  [3, 4]]
                IOType.Companion.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
                // [[5, 6],
                //  [7, 8]]
                IOType.Companion.d2(2, 2) { x, y -> (x * 2 + y + 5).toDouble() },
            )
        // [[(1+5)/2, (2+6)/2],
        //  [(3+7)/2, (4+8)/2]] = [[3.0, 4.0], [5.0, 6.0]]
        val result = input.average()
        assertEquals(
            expected = IOType.Companion.d2(
                2,
                2,
            ) { x, y -> ((x * 2 + y + 1) + (x * 2 + y + 5)) / 2.0 },
            actual = result,
        )
    }

    @Test
    fun `D3のaverage=各チャネルの平均`() {
        // [[[1, 2], [3, 4]],
        //  [[5, 6], [7, 8]]]
        val input = IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() }
        // [[(1+2)/2, (3+4)/2],
        //  [(5+6)/2, (7+8)/2]] = [[1.5, 3.5], [5.5, 7.5]]
        val result = input.average()
        assertEquals(
            expected =
            IOType.Companion.d2(2, 2) { x, y ->
                val z0 = x * 4 + y * 2 + 0 + 1
                val z1 = x * 4 + y * 2 + 1 + 1
                (z0 + z1) / 2.0
            },
            actual = result,
        )
    }

    @Test
    fun `List_D3のaverage=要素ごとの平均`() {
        val input =
            listOf(
                // [[[1, 2], [3, 4]],
                //  [[5, 6], [7, 8]]]
                IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
                // [[[9, 10], [11, 12]],
                //  [[13, 14], [15, 16]]]
                IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 9).toDouble() },
            )
        // [[(1+9)/2, (2+10)/2], [(3+11)/2, (4+12)/2]],
        //  [[(5+13)/2, (6+14)/2], [(7+15)/2, (8+16)/2]]]
        val result = input.average()
        assertEquals(expected = 5.0, actual = result[0, 0, 0])
        assertEquals(expected = 6.0, actual = result[0, 0, 1])
        assertEquals(expected = 7.0, actual = result[0, 1, 0])
        assertEquals(expected = 8.0, actual = result[0, 1, 1])
        assertEquals(expected = 9.0, actual = result[1, 0, 0])
        assertEquals(expected = 10.0, actual = result[1, 0, 1])
        assertEquals(expected = 11.0, actual = result[1, 1, 0])
        assertEquals(expected = 12.0, actual = result[1, 1, 1])
    }
}
