@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.reshape.reshape

import com.wsr.IOType
import com.wsr.layer.reshape.reshape.ReshapeD3ToD1
import kotlin.test.Test
import kotlin.test.assertEquals

class ReshapeD3ToD1Test {
    @Test
    fun `ReshapeD3ToD1の_expect=D3をD1に平坦化`() {
        val reshape = ReshapeD3ToD1(outputSize = 8)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
            )

        val result = reshape._expect(input)

        // [1, 2, 3, 4, 5, 6, 7, 8]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1
        assertEquals(expected = 1.0, actual = output[0])
        assertEquals(expected = 2.0, actual = output[1])
        assertEquals(expected = 3.0, actual = output[2])
        assertEquals(expected = 4.0, actual = output[3])
        assertEquals(expected = 5.0, actual = output[4])
        assertEquals(expected = 6.0, actual = output[5])
        assertEquals(expected = 7.0, actual = output[6])
        assertEquals(expected = 8.0, actual = output[7])
    }

    @Test
    fun `ReshapeD3ToD1の_train=D1のdeltaをD3に戻す`() {
        val reshape = ReshapeD3ToD1(outputSize = 8)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
            )

        // deltaは[2, 4, 6, 8, 10, 12, 14, 16]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1((1..8).map { it * 2.0 }))
        }

        val result = reshape._train(input, calcDelta)

        // [[[2, 4], [6, 8]], [[10, 12], [14, 16]]]
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D3
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
