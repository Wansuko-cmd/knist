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
    fun `List_D1のmax=各D1の最大値のリスト`() {
        val list =
            listOf(
                IOType.d1(listOf(5.0f, 2.0f, 8.0f)),
                IOType.d1(listOf(3.0f, 7.0f, 1.0f)),
                IOType.d1(listOf(9.0f, 4.0f, 6.0f)),
            )
        val result = list.max()
        assertEquals(
            expected = listOf(8.0f, 7.0f, 9.0f),
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
    fun `List_D2のmax=各D2の最大値のリスト`() {
        val list =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        val result = list.max()
        assertEquals(
            expected = listOf(4.0f, 8.0f),
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
    fun `List_D2のmax_axis0=各D2の列方向最大値のリスト`() {
        val list =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        val result = list.max(axis = 0)
        assertEquals(expected = 2, actual = result.size)
        assertEquals(expected = IOType.d1(listOf(3.0f, 4.0f)), actual = result[0])
        assertEquals(expected = IOType.d1(listOf(7.0f, 8.0f)), actual = result[1])
    }

    @Test
    fun `List_D2のmax_axis1=各D2の行方向最大値のリスト`() {
        val list =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        val result = list.max(axis = 1)
        assertEquals(expected = 2, actual = result.size)
        assertEquals(expected = IOType.d1(listOf(2.0f, 4.0f)), actual = result[0])
        assertEquals(expected = IOType.d1(listOf(6.0f, 8.0f)), actual = result[1])
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
    fun `List_D3のmax=各D3の最大値のリスト`() {
        val list =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 10).toFloat() },
            )
        val result = list.max()
        assertEquals(
            expected = listOf(8.0f, 17.0f),
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

    @Test
    fun `List_D3のmax_axis0=各D3のX軸方向最大値のリスト`() {
        val list =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 10).toFloat() },
            )
        val result = list.max(axis = 0)
        assertEquals(expected = 2, actual = result.size)
        val first = result[0]
        assertEquals(expected = 5.0f, actual = first[0, 0])
        assertEquals(expected = 6.0f, actual = first[0, 1])
        val second = result[1]
        assertEquals(expected = 14.0f, actual = second[0, 0])
        assertEquals(expected = 15.0f, actual = second[0, 1])
    }

    @Test
    fun `List_D3のmax_axis1=各D3のY軸方向最大値のリスト`() {
        val list =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 10).toFloat() },
            )
        val result = list.max(axis = 1)
        assertEquals(expected = 2, actual = result.size)
        val first = result[0]
        assertEquals(expected = 3.0f, actual = first[0, 0])
        assertEquals(expected = 4.0f, actual = first[0, 1])
        val second = result[1]
        assertEquals(expected = 12.0f, actual = second[0, 0])
        assertEquals(expected = 13.0f, actual = second[0, 1])
    }

    @Test
    fun `List_D3のmax_axis2=各D3のZ軸方向最大値のリスト`() {
        val list =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 10).toFloat() },
            )
        val result = list.max(axis = 2)
        assertEquals(expected = 2, actual = result.size)
        val first = result[0]
        assertEquals(expected = 2.0f, actual = first[0, 0])
        assertEquals(expected = 4.0f, actual = first[0, 1])
        val second = result[1]
        assertEquals(expected = 11.0f, actual = second[0, 0])
        assertEquals(expected = 13.0f, actual = second[0, 1])
    }
}
