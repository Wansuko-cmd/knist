@file:Suppress("NonAsciiCharacters")

package com.wsr.power

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class PowExtTest {
    @Test
    fun `D1のpow=各要素をn乗する`() {
        val a = IOType.d1(listOf(2.0, 3.0, 4.0))
        val result = a.pow(2)

        assertEquals(expected = 4.0, actual = result[0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.0, actual = result[1], absoluteTolerance = 1e-10)
        assertEquals(expected = 16.0, actual = result[2], absoluteTolerance = 1e-10)
    }

    @Test
    fun `D2のpow=各要素をn乗する`() {
        // [[2, 3], [4, 5]]
        val a = IOType.d2(2, 2) { x, y -> (x * 2 + y + 2).toFloat() }
        val result = a.pow(2)

        // [[4, 9], [16, 25]]
        assertEquals(expected = 4.0, actual = result[0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.0, actual = result[0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 16.0, actual = result[1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 25.0, actual = result[1, 1], absoluteTolerance = 1e-10)
    }

    @Test
    fun `D3のpow=各要素をn乗する`() {
        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a.pow(2)

        // [[[1, 4], [9, 16]], [[25, 36], [49, 64]]]
        assertEquals(expected = 1.0, actual = result[0, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 4.0, actual = result[0, 0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 9.0, actual = result[0, 1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 16.0, actual = result[0, 1, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 25.0, actual = result[1, 0, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 36.0, actual = result[1, 0, 1], absoluteTolerance = 1e-10)
        assertEquals(expected = 49.0, actual = result[1, 1, 0], absoluteTolerance = 1e-10)
        assertEquals(expected = 64.0, actual = result[1, 1, 1], absoluteTolerance = 1e-10)
    }
}
