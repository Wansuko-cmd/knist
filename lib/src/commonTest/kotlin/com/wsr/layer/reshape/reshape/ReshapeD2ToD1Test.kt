@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.reshape.reshape

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.batchOf
import com.wsr.core.d1
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.layer.Context
import com.wsr.layer.reshape.reshape.ReshapeD2ToD1
import kotlin.test.Test
import kotlin.test.assertEquals

class ReshapeD2ToD1Test {
    @Test
    fun `ReshapeD2ToD1の_expect=D2をD1に平坦化`() {
        val reshape = ReshapeD2ToD1(outputSize = 4)

        // [[1, 2], [3, 4]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val context = Context(input)

        val result = reshape._expect(input, context) as Batch<IOType.D1>
        // [1, 2, 3, 4]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 1.0f, actual = output[0])
        assertEquals(expected = 2.0f, actual = output[1])
        assertEquals(expected = 3.0f, actual = output[2])
        assertEquals(expected = 4.0f, actual = output[3])
    }

    @Test
    fun `ReshapeD2ToD1の_train=D1のdeltaをD2に戻す`() {
        val reshape = ReshapeD2ToD1(outputSize = 4)

        // [[1, 2], [3, 4]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[2, 4, 6, 8]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(2.0f, 4.0f, 6.0f, 8.0f)))
        }

        val result = reshape._train(input, context, calcDelta) as Batch<IOType.D2>
        // [[2, 4], [6, 8]]
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = 2.0f, actual = dx[0, 0])
        assertEquals(expected = 4.0f, actual = dx[0, 1])
        assertEquals(expected = 6.0f, actual = dx[1, 0])
        assertEquals(expected = 8.0f, actual = dx[1, 1])
    }
}
