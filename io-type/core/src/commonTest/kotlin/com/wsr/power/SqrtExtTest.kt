@file:Suppress("NonAsciiCharacters")

package com.wsr.power

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class SqrtExtTest {
    @Test
    fun `D1のsqrt=各要素の平方根を計算する`() {
        val a = IOType.d1(listOf(4.0, 9.0, 16.0))
        val result = a.sqrt(e = 0.0)

        assertEquals(expected = 2.0, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 3.0, actual = result[1], absoluteTolerance = 1e-10)
        assertEquals(expected = 4.0, actual = result[2], absoluteTolerance = 1e-10)
    }

    @Test
    fun `D2のsqrt=各要素の平方根を計算する`() {
        // [[4, 9], [16, 25]]
        val a = IOType.d2(2, 2) { x, y ->
            when {
                x == 0 && y == 0 -> 4.0
                x == 0 && y == 1 -> 9.0
                x == 1 && y == 0 -> 16.0
                else -> 25.0
            }
        }
        val result = a.sqrt(e = 0.0)

        // [[2, 3], [4, 5]]
        assertEquals(expected = 2.0, actual = result[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 3.0, actual = result[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 4.0, actual = result[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 5.0, actual = result[1, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `D3のsqrt=各要素の平方根を計算する`() {
        // [[[1, 4], [9, 16]], [[25, 36], [49, 64]]]
        val a = IOType.d3(2, 2, 2) { x, y, z ->
            val idx = x * 4 + y * 2 + z + 1
            (idx * idx).toDouble()
        }
        val result = a.sqrt(e = 0.0)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        assertEquals(expected = 1.0, actual = result[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 2.0, actual = result[0, 0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 3.0, actual = result[0, 1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 4.0, actual = result[0, 1, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 5.0, actual = result[1, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 6.0, actual = result[1, 0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 7.0, actual = result[1, 1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 8.0, actual = result[1, 1, 1], absoluteTolerance = 1e-10)
    }
}
