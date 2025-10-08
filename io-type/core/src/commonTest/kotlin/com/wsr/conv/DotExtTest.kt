@file:Suppress("NonAsciiCharacters")

package com.wsr.conv

import kotlin.test.Test
import kotlin.test.assertEquals

class DotExtTest {
    @Test
    fun `DoubleArray·DoubleArray=内積`() {
        val a = doubleArrayOf(1.0, 2.0, 3.0)
        val b = doubleArrayOf(4.0, 5.0, 6.0)
        val result = a dot b
        assertEquals(
            expected = 32.0,
            actual = result,
        )
    }

    @Test
    fun `Array_DoubleArray·Array_DoubleArray=行列積`() {
        // [[1, 2],
        //  [3, 4]]
        val a =
            arrayOf(
                doubleArrayOf(1.0, 2.0),
                doubleArrayOf(3.0, 4.0),
            )
        // [[1, 2],
        //  [3, 4]]
        val b =
            arrayOf(
                doubleArrayOf(1.0, 2.0),
                doubleArrayOf(3.0, 4.0),
            )
        val result = a dot b

        // 実装: result[f][i] = this[i] dot other[f]
        // result[0][0] = a[0] dot b[0] = 1*1 + 2*2 = 5
        // result[0][1] = a[1] dot b[0] = 3*1 + 4*2 = 11
        // result[1][0] = a[0] dot b[1] = 1*3 + 2*4 = 11
        // result[1][1] = a[1] dot b[1] = 3*3 + 4*4 = 25
        assertEquals(expected = 2, actual = result.size)
        assertEquals(expected = listOf(5.0, 11.0), actual = result[0].toList())
        assertEquals(expected = listOf(11.0, 25.0), actual = result[1].toList())
    }
}
