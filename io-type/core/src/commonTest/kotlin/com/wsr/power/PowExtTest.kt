@file:Suppress("NonAsciiCharacters")

package com.wsr.power

import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

class PowExtTest {
    @Test
    fun `D1のpow=各要素をn乗する`() {
        val a = IOType.d1(listOf(2.0f, 3.0f, 4.0f))
        val result = a.pow(2)

        assertEquals(expected = 4.0f, actual = result[0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 9.0f, actual = result[1], absoluteTolerance = 1e-10f)
        assertEquals(expected = 16.0f, actual = result[2], absoluteTolerance = 1e-10f)
    }

    @Test
    fun `D2のpow=各要素をn乗する`() {
        // [[2, 3], [4, 5]]
        val a = IOType.d2(2, 2) { x, y -> (x * 2 + y + 2).toFloat() }
        val result = a.pow(2)

        // [[4, 9], [16, 25]]
        assertEquals(expected = 4.0f, actual = result[0, 0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 9.0f, actual = result[0, 1], absoluteTolerance = 1e-10f)
        assertEquals(expected = 16.0f, actual = result[1, 0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 25.0f, actual = result[1, 1], absoluteTolerance = 1e-10f)
    }

    @Test
    fun `D3のpow=各要素をn乗する`() {
        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a.pow(2)

        // [[[1, 4], [9, 16]], [[25, 36], [49, 64]]]
        assertEquals(expected = 1.0f, actual = result[0, 0, 0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 4.0f, actual = result[0, 0, 1], absoluteTolerance = 1e-10f)
        assertEquals(expected = 9.0f, actual = result[0, 1, 0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 16.0f, actual = result[0, 1, 1], absoluteTolerance = 1e-10f)
        assertEquals(expected = 25.0f, actual = result[1, 0, 0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 36.0f, actual = result[1, 0, 1], absoluteTolerance = 1e-10f)
        assertEquals(expected = 49.0f, actual = result[1, 1, 0], absoluteTolerance = 1e-10f)
        assertEquals(expected = 64.0f, actual = result[1, 1, 1], absoluteTolerance = 1e-10f)
    }
}
