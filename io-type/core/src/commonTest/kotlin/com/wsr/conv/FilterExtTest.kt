@file:Suppress("NonAsciiCharacters")

package com.wsr.conv

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class FilterExtTest {
    @Test
    fun `D3のtoFilter=フィルタ形式の配列に変換`() {
        // shape: [2, 2, 3] (filterCount=2, channels=2, kernel=3)
        val weight = IOType.d3(2, 2, 3) { f, c, k -> (f * 6 + c * 3 + k + 1).toDouble() }
        val result = weight.toFilter()

        // フィルタ0: channel0=[1,2,3], channel1=[4,5,6] -> [1,2,3,4,5,6]
        assertEquals(
            expected = listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0),
            actual = result[0].toList(),
        )
        // フィルタ1: channel0=[7,8,9], channel1=[10,11,12] -> [7,8,9,10,11,12]
        assertEquals(
            expected = listOf(7.0, 8.0, 9.0, 10.0, 11.0, 12.0),
            actual = result[1].toList(),
        )
    }
}
