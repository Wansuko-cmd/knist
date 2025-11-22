@file:Suppress("NonAsciiCharacters")

package com.wsr.collection

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class MaxExtTest {
    @Test
    fun `D1のmax=全要素の最大値`() {
        val a = IOType.d1(listOf(3.0f, 1.0f, 4.0f, 2.0f))
        val result = a.max()
        assertEquals(
            expected = 4.0f,
            actual = result,
        )
    }

    @Test
    fun `D2のmax=全要素の最大値`() {
        // [[1, 2, 3],
        //  [4, 5, 6]]
        val a = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        val result = a.max()
        assertEquals(
            expected = 6.0f,
            actual = result,
        )
    }

    @Test
    fun `D2のmax_axis0=各列の最大値`() {
        // [[1, 2],
        //  [3, 4],
        //  [5, 6]]
        val a = IOType.d2(3, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val result = a.max(axis = 0)
        assertEquals(
            expected = IOType.d1(listOf(5.0f, 6.0f)),
            actual = result,
        )
    }

    @Test
    fun `D2のmax_axis1=各行の最大値`() {
        // [[1, 2],
        //  [3, 4],
        //  [5, 6]]
        val a = IOType.d2(3, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val result = a.max(axis = 1)
        assertEquals(
            expected = IOType.d1(listOf(2.0f, 4.0f, 6.0f)),
            actual = result,
        )
    }

    @Test
    fun `D3のmax=全要素の最大値`() {
        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a.max()
        assertEquals(
            expected = 8.0f,
            actual = result,
        )
    }

    @Test
    fun `D3のmax_axis0=各YZ平面の最大値`() {
        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a.max(axis = 0)
        val expected = IOType.d2(2, 2) { y, z -> (4 + y * 2 + z + 1).toFloat() }
        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `D3のmax_axis1=各XZ平面の最大値`() {
        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a.max(axis = 1)
        // Expected: [[3, 4], [7, 8]]
        assertEquals(expected = 2, actual = result.shape[0])
        assertEquals(expected = 2, actual = result.shape[1])
        assertEquals(expected = 3.0f, actual = result[0, 0])
        assertEquals(expected = 4.0f, actual = result[0, 1])
        assertEquals(expected = 7.0f, actual = result[1, 0])
        assertEquals(expected = 8.0f, actual = result[1, 1])
    }

    @Test
    fun `D3のmax_axis2=各XY平面の最大値`() {
        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a.max(axis = 2)
        // Expected: [[2, 4], [6, 8]]
        assertEquals(expected = 2, actual = result.shape[0])
        assertEquals(expected = 2, actual = result.shape[1])
        assertEquals(expected = 2.0f, actual = result[0, 0])
        assertEquals(expected = 4.0f, actual = result[0, 1])
        assertEquals(expected = 6.0f, actual = result[1, 0])
        assertEquals(expected = 8.0f, actual = result[1, 1])
    }
}
