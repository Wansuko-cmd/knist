@file:Suppress("NonAsciiCharacters")

package com.wsr.conv

import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.operation.conv.toFilter
import kotlin.test.Test
import kotlin.test.assertEquals

class FilterExtTest {
    @Test
    fun `D3のtoFilter=フィルタ形式の配列に変換`() {
        // shape: [2, 2, 3] (filterCount=2, channels=2, kernel=3)
        val weight = IOType.d3(2, 2, 3) { f, c, k -> (f * 6 + c * 3 + k + 1).toFloat() }
        val result = weight.toFilter()

        // フィルタ0: channel0=[1,2,3], channel1=[4,5,6] -> [1,2,3,4,5,6]
        assertEquals(
            expected = listOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f),
            actual = result[0].toList(),
        )
        // フィルタ1: channel0=[7,8,9], channel1=[10,11,12] -> [7,8,9,10,11,12]
        assertEquals(
            expected = listOf(7.0f, 8.0f, 9.0f, 10.0f, 11.0f, 12.0f),
            actual = result[1].toList(),
        )
    }
}
