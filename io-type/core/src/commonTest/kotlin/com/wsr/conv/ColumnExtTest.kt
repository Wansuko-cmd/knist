@file:Suppress("NonAsciiCharacters")

package com.wsr.conv

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class ColumnExtTest {
    @Test
    fun `List_D2のtoColumn=im2col変換_kernel3_stride1_padding0`() {
        // [[1, 2, 3, 4, 5]]
        val input =
            listOf(
                IOType.d2(1, 5) { _, y -> (y + 1).toFloat() },
            )
        // kernel=3, stride=1, padding=0
        // output size = (5 - 3 + 0) / 1 + 1 = 3
        val result = input.toColumn(kernel = 3, stride = 1, padding = 0)

        assertEquals(expected = 3, actual = result.size)
        // 位置0: [1, 2, 3]
        assertEquals(expected = listOf(1.0, 2.0, 3.0), actual = result[0].toList())
        // 位置1: [2, 3, 4]
        assertEquals(expected = listOf(2.0, 3.0, 4.0), actual = result[1].toList())
        // 位置2: [3, 4, 5]
        assertEquals(expected = listOf(3.0, 4.0, 5.0), actual = result[2].toList())
    }

    @Test
    fun `List_D2のtoColumn=im2col変換_kernel3_stride2_padding0`() {
        // [[1, 2, 3, 4, 5]]
        val input =
            listOf(
                IOType.d2(1, 5) { _, y -> (y + 1).toFloat() },
            )
        // kernel=3, stride=2, padding=0
        // output size = (5 - 3 + 0) / 2 + 1 = 2
        val result = input.toColumn(kernel = 3, stride = 2, padding = 0)

        assertEquals(expected = 2, actual = result.size)
        // 位置0: [1, 2, 3]
        assertEquals(expected = listOf(1.0, 2.0, 3.0), actual = result[0].toList())
        // 位置1: [3, 4, 5]
        assertEquals(expected = listOf(3.0, 4.0, 5.0), actual = result[1].toList())
    }

    @Test
    fun `List_D2のtoColumn=im2col変換_kernel3_stride1_padding1`() {
        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d2(1, 3) { _, y -> (y + 1).toFloat() },
            )
        // kernel=3, stride=1, padding=1
        // output size = (3 - 3 + 2) / 1 + 1 = 3
        val result = input.toColumn(kernel = 3, stride = 1, padding = 1)

        assertEquals(expected = 3, actual = result.size)
        // 位置0: [0, 1, 2] (左にpadding)
        assertEquals(expected = listOf(0.0, 1.0, 2.0), actual = result[0].toList())
        // 位置1: [1, 2, 3]
        assertEquals(expected = listOf(1.0, 2.0, 3.0), actual = result[1].toList())
        // 位置2: [2, 3, 0] (右にpadding)
        assertEquals(expected = listOf(2.0, 3.0, 0.0), actual = result[2].toList())
    }
}
