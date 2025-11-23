@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.function.linear

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.batchOf
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.layer.Context
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearD3Test {
    @Test
    fun `LinearD3の_expect=入力をそのまま返す`() {
        val linear = LinearD3(outputX = 2, outputY = 2, outputZ = 2)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        val result = linear._expect(input, context) as Batch<IOType.D3>
        assertEquals(expected = input, actual = result)
    }

    @Test
    fun `LinearD3の_train=deltaをそのまま返す`() {
        val linear = LinearD3(outputX = 2, outputY = 2, outputZ = 2)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは入力の2倍
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1) * 2.0f })
        }

        val result = linear._train(input, context, calcDelta) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        // deltaは入力の2倍
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
