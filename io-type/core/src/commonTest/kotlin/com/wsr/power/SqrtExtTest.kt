@file:Suppress("NonAsciiCharacters")

package com.wsr.power

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class SqrtExtTest {
    @Test
    fun `D1のsqrt=各要素の平方根を計算する`() {
        val a = IOType.d1(listOf(4.0f, 9.0f, 16.0f))
        val result = a.sqrt(e = 0.0f)

        assertEquals(expected = 2.0f, actual = result[0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 3.0f, actual = result[1], absoluteTolerance = 1e-10f)
        assertEquals(expected = 4.0f, actual = result[2], absoluteTolerance = 1e-10f)
    }

    @Test
    fun `D2のsqrt=各要素の平方根を計算する`() {
        // [[4, 9], [16, 25]]
        val a = IOType.d2(2, 2) { x, y ->
            when {
                x == 0 && y == 0 -> 4.0f
                x == 0 && y == 1 -> 9.0f
                x == 1 && y == 0 -> 16.0f
                else -> 25.0f
            }
        }
        val result = a.sqrt(e = 0.0f)

        // [[2, 3], [4, 5]]
        assertEquals(expected = 2.0f, actual = result[0, 0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 3.0f, actual = result[0, 1], absoluteTolerance = 1e-10f)
        assertEquals(expected = 4.0f, actual = result[1, 0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 5.0f, actual = result[1, 1], absoluteTolerance = 1e-10f)
    }

    @Test
    fun `D3のsqrt=各要素の平方根を計算する`() {
        // [[[1, 4], [9, 16]], [[25, 36], [49, 64]]]
        val a = IOType.d3(2, 2, 2) { x, y, z ->
            val idx = x * 4 + y * 2 + z + 1
            (idx * idx).toFloat()
        }
        val result = a.sqrt(e = 0.0f)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        assertEquals(expected = 1.0f, actual = result[0, 0, 0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 2.0f, actual = result[0, 0, 1], absoluteTolerance = 1e-10f)
        assertEquals(expected = 3.0f, actual = result[0, 1, 0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 4.0f, actual = result[0, 1, 1], absoluteTolerance = 1e-10f)
        assertEquals(expected = 5.0f, actual = result[1, 0, 0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 6.0f, actual = result[1, 0, 1], absoluteTolerance = 1e-10f)
        assertEquals(expected = 7.0f, actual = result[1, 1, 0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 8.0f, actual = result[1, 1, 1], absoluteTolerance = 1e-10f)
    }
}
