@file:Suppress("NonAsciiCharacters")

package com.wsr.collection

import com.wsr.IOType
import com.wsr.d1
import com.wsr.d2
import com.wsr.d3
import com.wsr.get
import kotlin.test.Test
import kotlin.test.assertEquals

class AverageExtTest {
    @Test
    fun `D1のaverage=平均値`() {
        // [1, 2, 3, 4, 5]
        val input = IOType.Companion.d1(listOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f))
        // (1 + 2 + 3 + 4 + 5) / 5 = 3.0
        val result = input.average()
        assertEquals(expected = 3.0f, actual = result)
    }

    @Test
    fun `D2のaverage=全体の平均`() {
        // [[1, 2, 3],
        //  [4, 5, 6]]
        val input = IOType.Companion.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        // (1+2+3+4+5+6)/6 = 21/6 = 3.5
        val result = input.average()
        assertEquals(expected = 3.5f, actual = result)
    }

    @Test
    fun `D3のaverage=全体の平均`() {
        // [[[1, 2], [3, 4]],
        //  [[5, 6], [7, 8]]]
        val input = IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        // (1+2+3+4+5+6+7+8)/8 = 36/8 = 4.5
        val result = input.average()
        assertEquals(expected = 4.5f, actual = result)
    }

    @Test
    fun `D2のaverage_axis0=列方向の平均`() {
        // [[1, 2, 3],
        //  [4, 5, 6]]
        val input = IOType.Companion.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        // axis=0: 各列の平均 = [(1+4)/2, (2+5)/2, (3+6)/2] = [2.5, 3.5, 4.5]
        val result = input.average(axis = 0)
        assertEquals(expected = IOType.Companion.d1(listOf(2.5f, 3.5f, 4.5f)), actual = result)
    }

    @Test
    fun `D2のaverage_axis1=行方向の平均`() {
        // [[1, 2, 3],
        //  [4, 5, 6]]
        val input = IOType.Companion.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() }
        // axis=1: 各行の平均 = [(1+2+3)/3, (4+5+6)/3] = [2.0, 5.0]
        val result = input.average(axis = 1)
        assertEquals(expected = IOType.Companion.d1(listOf(2.0f, 5.0f)), actual = result)
    }

    @Test
    fun `D3のaverage_axis0=第0軸方向の平均`() {
        // [[[1, 2], [3, 4]],
        //  [[5, 6], [7, 8]]]
        val input = IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        // axis=0: [[(1+5)/2, (2+6)/2], [(3+7)/2, (4+8)/2]] = [[3.0, 4.0], [5.0, 6.0]]
        val result = input.average(axis = 0)
        assertEquals(
            expected = IOType.Companion.d2(2, 2) { y, z -> ((0 * 4 + y * 2 + z + 1) + (1 * 4 + y * 2 + z + 1)) / 2.0f },
            actual = result,
        )
    }

    @Test
    fun `D3のaverage_axis1=第1軸方向の平均`() {
        // [[[1, 2], [3, 4]],
        //  [[5, 6], [7, 8]]]
        val input = IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        // axis=1: [[(1+3)/2, (2+4)/2], [(5+7)/2, (6+8)/2]] = [[2.0, 3.0], [6.0, 7.0]]
        val result = input.average(axis = 1)
        assertEquals(
            expected = IOType.Companion.d2(2, 2) { x, z -> ((x * 4 + 0 * 2 + z + 1) + (x * 4 + 1 * 2 + z + 1)) / 2.0f },
            actual = result,
        )
    }

    @Test
    fun `D3のaverage_axis2=第2軸方向の平均`() {
        // [[[1, 2], [3, 4]],
        //  [[5, 6], [7, 8]]]
        val input = IOType.Companion.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() }
        // axis=2: [[(1+2)/2, (3+4)/2], [(5+6)/2, (7+8)/2]] = [[1.5, 3.5], [5.5, 7.5]]
        val result = input.average(axis = 2)
        assertEquals(
            expected = IOType.Companion.d2(2, 2) { x, y -> ((x * 4 + y * 2 + 0 + 1) + (x * 4 + y * 2 + 1 + 1)) / 2.0f },
            actual = result,
        )
    }
}
