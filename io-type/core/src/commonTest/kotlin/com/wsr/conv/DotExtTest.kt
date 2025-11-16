@file:Suppress("NonAsciiCharacters")

package com.wsr.conv

import kotlin.test.Test
import kotlin.test.assertEquals

class DotExtTest {
    @Test
    fun `FloatArray·FloatArray=内積`() {
        val a = floatArrayOf(1.0f, 2.0f, 3.0f)
        val b = floatArrayOf(4.0f, 5.0f, 6.0f)
        val result = a dot b
        assertEquals(
            expected = 32.0f,
            actual = result,
        )
    }

    @Test
    fun `Array_FloatArray·Array_FloatArray=行列積`() {
        // [[1, 2],
        //  [3, 4]]
        val a =
            arrayOf(
                floatArrayOf(1.0f, 2.0f),
                floatArrayOf(3.0f, 4.0f),
            )
        // [[1, 2],
        //  [3, 4]]
        val b =
            arrayOf(
                floatArrayOf(1.0f, 2.0f),
                floatArrayOf(3.0f, 4.0f),
            )
        val result = a dot b

        // 実装: result[f][i] = this[i] dot other[f]
        // result[0][0] = a[0] dot b[0] = 1*1 + 2*2 = 5
        // result[0][1] = a[1] dot b[0] = 3*1 + 4*2 = 11
        // result[1][0] = a[0] dot b[1] = 1*3 + 2*4 = 11
        // result[1][1] = a[1] dot b[1] = 3*3 + 4*4 = 25
        assertEquals(expected = 2, actual = result.size)
        assertEquals(expected = listOf(5.0f, 11.0f), actual = result[0].toList())
        assertEquals(expected = listOf(11.0f, 25.0f), actual = result[1].toList())
    }
}
