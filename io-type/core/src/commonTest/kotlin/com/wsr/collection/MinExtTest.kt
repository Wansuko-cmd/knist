@file:Suppress("NonAsciiCharacters")

package com.wsr.collection

import com.wsr.core.IOType
import com.wsr.collection.min.min
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import kotlin.test.Test
import kotlin.test.assertEquals

class MinExtTest {
    @Test
    fun `D1のmin=全要素の最小値`() {
        val a = IOType.d1(listOf(3.0f, 1.0f, 4.0f, 2.0f))
        val result = a.min()
        assertEquals(
            expected = 1.0f,
            actual = result,
        )
    }

    @Test
    fun `D2のmin=全要素の最小値`() {
        // [[1, 2, 3],
        //  [4, 5, 6]]
        val a = IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        val result = a.min()
        assertEquals(
            expected = 1.0f,
            actual = result,
        )
    }

    @Test
    fun `D2のmin_axis0=各列の最小値`() {
        // [[1, 2],
        //  [3, 4],
        //  [5, 6]]
        val a = IOType.d2(3, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val result = a.min(axis = 0)
        assertEquals(
            expected = IOType.d1(listOf(1.0f, 2.0f)),
            actual = result,
        )
    }

    @Test
    fun `D2のmin_axis1=各行の最小値`() {
        // [[1, 2],
        //  [3, 4],
        //  [5, 6]]
        val a = IOType.d2(3, 2) { x, y -> (x * 2 + y + 1).toFloat() }
        val result = a.min(axis = 1)
        assertEquals(
            expected = IOType.d1(listOf(1.0f, 3.0f, 5.0f)),
            actual = result,
        )
    }

    @Test
    fun `D3のmin=全要素の最小値`() {
        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a.min()
        assertEquals(
            expected = 1.0f,
            actual = result,
        )
    }

    @Test
    fun `D3のmin_axis0=各YZ平面の最小値`() {
        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a.min(axis = 0)
        val expected = IOType.d2(2, 2) { y, z -> (y * 2 + z + 1).toFloat() }
        assertEquals(expected = expected, actual = result)
    }

    @Test
    fun `D3のmin_axis1=各XZ平面の最小値`() {
        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a.min(axis = 1)
        // Expected: [[1, 2], [5, 6]]
        assertEquals(expected = 2, actual = result.shape[0])
        assertEquals(expected = 2, actual = result.shape[1])
        assertEquals(expected = 1.0f, actual = result[0, 0])
        assertEquals(expected = 2.0f, actual = result[0, 1])
        assertEquals(expected = 5.0f, actual = result[1, 0])
        assertEquals(expected = 6.0f, actual = result[1, 1])
    }

    @Test
    fun `D3のmin_axis2=各XY平面の最小値`() {
        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a.min(axis = 2)
        // Expected: [[1, 3], [5, 7]]
        assertEquals(expected = 2, actual = result.shape[0])
        assertEquals(expected = 2, actual = result.shape[1])
        assertEquals(expected = 1.0f, actual = result[0, 0])
        assertEquals(expected = 3.0f, actual = result[0, 1])
        assertEquals(expected = 5.0f, actual = result[1, 0])
        assertEquals(expected = 7.0f, actual = result[1, 1])
    }
}
