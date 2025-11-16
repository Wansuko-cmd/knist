@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.linear

import com.wsr.IOType
import com.wsr.layer.process.function.linear.LinearD3
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearD3Test {
    @Test
    fun `LinearD3の_expect=入力をそのまま返す`() {
        val linear = LinearD3(outputX = 2, outputY = 2, outputZ = 2)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )

        val result = linear._expect(input)

        assertEquals(expected = input, actual = result)
    }

    @Test
    fun `LinearD3の_train=deltaをそのまま返す`() {
        val linear = LinearD3(outputX = 2, outputY = 2, outputZ = 2)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )

        // deltaは入力の2倍
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1) * 2.0 })
        }

        val result = linear._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D3
        // deltaは入力の2倍
        assertEquals(expected = 2.0, actual = dx[0, 0, 0])
        assertEquals(expected = 4.0, actual = dx[0, 0, 1])
        assertEquals(expected = 6.0, actual = dx[0, 1, 0])
        assertEquals(expected = 8.0, actual = dx[0, 1, 1])
        assertEquals(expected = 10.0, actual = dx[1, 0, 0])
        assertEquals(expected = 12.0, actual = dx[1, 0, 1])
        assertEquals(expected = 14.0, actual = dx[1, 1, 0])
        assertEquals(expected = 16.0, actual = dx[1, 1, 1])
    }
}
