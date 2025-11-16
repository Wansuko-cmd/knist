@file:Suppress("NonAsciiCharacters")

package com.wsr.collection

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class MinExtTest {
    @Test
    fun `D1のmin=全要素の最小値`() {
        val a = IOType.d1(listOf(3.0, 1.0, 4.0, 2.0))
        val result = a.min()
        assertEquals(
            expected = 1.0,
            actual = result,
        )
    }

    @Test
    fun `List_D1のmin=各D1の最小値のリスト`() {
        val list =
            listOf(
                IOType.d1(listOf(5.0, 2.0, 8.0)),
                IOType.d1(listOf(3.0, 7.0, 1.0)),
                IOType.d1(listOf(9.0, 4.0, 6.0)),
            )
        val result = list.min()
        assertEquals(
            expected = listOf(2.0, 1.0, 4.0),
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
            expected = 1.0,
            actual = result,
        )
    }

    @Test
    fun `List_D2のmin=各D2の最小値のリスト`() {
        val list =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        val result = list.min()
        assertEquals(
            expected = listOf(1.0, 5.0),
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
            expected = IOType.d1(listOf(1.0, 2.0)),
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
            expected = IOType.d1(listOf(1.0, 3.0, 5.0)),
            actual = result,
        )
    }

    @Test
    fun `List_D2のmin_axis0=各D2の列方向最小値のリスト`() {
        val list =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        val result = list.min(axis = 0)
        assertEquals(expected = 2, actual = result.size)
        assertEquals(expected = IOType.d1(listOf(1.0, 2.0)), actual = result[0])
        assertEquals(expected = IOType.d1(listOf(5.0, 6.0)), actual = result[1])
    }

    @Test
    fun `List_D2のmin_axis1=各D2の行方向最小値のリスト`() {
        val list =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 5).toFloat() },
            )
        val result = list.min(axis = 1)
        assertEquals(expected = 2, actual = result.size)
        assertEquals(expected = IOType.d1(listOf(1.0, 3.0)), actual = result[0])
        assertEquals(expected = IOType.d1(listOf(5.0, 7.0)), actual = result[1])
    }

    @Test
    fun `D3のmin=全要素の最小値`() {
        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a.min()
        assertEquals(
            expected = 1.0,
            actual = result,
        )
    }

    @Test
    fun `List_D3のmin=各D3の最小値のリスト`() {
        val list =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 10).toFloat() },
            )
        val result = list.min()
        assertEquals(
            expected = listOf(1.0, 10.0),
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
        assertEquals(expected = 1.0, actual = result[0, 0])
        assertEquals(expected = 2.0, actual = result[0, 1])
        assertEquals(expected = 5.0, actual = result[1, 0])
        assertEquals(expected = 6.0, actual = result[1, 1])
    }

    @Test
    fun `D3のmin_axis2=各XY平面の最小値`() {
        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val a = IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        val result = a.min(axis = 2)
        // Expected: [[1, 3], [5, 7]]
        assertEquals(expected = 2, actual = result.shape[0])
        assertEquals(expected = 2, actual = result.shape[1])
        assertEquals(expected = 1.0, actual = result[0, 0])
        assertEquals(expected = 3.0, actual = result[0, 1])
        assertEquals(expected = 5.0, actual = result[1, 0])
        assertEquals(expected = 7.0, actual = result[1, 1])
    }

    @Test
    fun `List_D3のmin_axis0=各D3のX軸方向最小値のリスト`() {
        val list =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 10).toFloat() },
            )
        val result = list.min(axis = 0)
        assertEquals(expected = 2, actual = result.size)
        val first = result[0]
        assertEquals(expected = 1.0, actual = first[0, 0])
        assertEquals(expected = 2.0, actual = first[0, 1])
        val second = result[1]
        assertEquals(expected = 10.0, actual = second[0, 0])
        assertEquals(expected = 11.0, actual = second[0, 1])
    }

    @Test
    fun `List_D3のmin_axis1=各D3のY軸方向最小値のリスト`() {
        val list =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 10).toFloat() },
            )
        val result = list.min(axis = 1)
        assertEquals(expected = 2, actual = result.size)
        val first = result[0]
        assertEquals(expected = 1.0, actual = first[0, 0])
        assertEquals(expected = 2.0, actual = first[0, 1])
        val second = result[1]
        assertEquals(expected = 10.0, actual = second[0, 0])
        assertEquals(expected = 11.0, actual = second[0, 1])
    }

    @Test
    fun `List_D3のmin_axis2=各D3のZ軸方向最小値のリスト`() {
        val list =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 10).toFloat() },
            )
        val result = list.min(axis = 2)
        assertEquals(expected = 2, actual = result.size)
        val first = result[0]
        assertEquals(expected = 1.0, actual = first[0, 0])
        assertEquals(expected = 3.0, actual = first[0, 1])
        val second = result[1]
        assertEquals(expected = 10.0, actual = second[0, 0])
        assertEquals(expected = 12.0, actual = second[0, 1])
    }
}
