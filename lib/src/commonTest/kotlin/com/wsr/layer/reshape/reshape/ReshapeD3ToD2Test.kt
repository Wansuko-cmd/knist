@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.reshape.reshape

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.layer.Context
import com.wsr.layer.reshape.reshape.ReshapeD3ToD2
import kotlin.test.Test
import kotlin.test.assertEquals

class ReshapeD3ToD2Test {
    @Test
    fun `ReshapeD3ToD2の_expect=D3をD2に平坦化`() {
        val reshape = ReshapeD3ToD2(outputX = 2, outputY = 4)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        val result = reshape._expect(input, context) as Batch<IOType.D2>
        // [[1, 2, 3, 4], [5, 6, 7, 8]]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 1.0f, actual = output[0, 0])
        assertEquals(expected = 2.0f, actual = output[0, 1])
        assertEquals(expected = 3.0f, actual = output[0, 2])
        assertEquals(expected = 4.0f, actual = output[0, 3])
        assertEquals(expected = 5.0f, actual = output[1, 0])
        assertEquals(expected = 6.0f, actual = output[1, 1])
        assertEquals(expected = 7.0f, actual = output[1, 2])
        assertEquals(expected = 8.0f, actual = output[1, 3])
    }

    @Test
    fun `ReshapeD3ToD2の_train=D2のdeltaをD3に戻す`() {
        val reshape = ReshapeD3ToD2(outputX = 2, outputY = 4)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[2, 4, 6, 8], [10, 12, 14, 16]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 4) { x, y -> (x * 4 + y + 1) * 2.0f })
        }

        val result = reshape._train(input, context, calcDelta) as Batch<IOType.D3>
        // [[[2, 4], [6, 8]], [[10, 12], [14, 16]]]
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = 2.0f, actual = dx[0, 0, 0])
        assertEquals(expected = 4.0f, actual = dx[0, 0, 1])
        assertEquals(expected = 6.0f, actual = dx[0, 1, 0])
        assertEquals(expected = 8.0f, actual = dx[0, 1, 1])
        assertEquals(expected = 10.0f, actual = dx[1, 0, 0])
        assertEquals(expected = 12.0f, actual = dx[1, 0, 1])
        assertEquals(expected = 14.0f, actual = dx[1, 1, 0])
        assertEquals(expected = 16.0f, actual = dx[1, 1, 1])
    }
}
