@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.function.linear

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.d2
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.function.linear.LinearD2
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearD2Test {
    @Test
    fun `LinearD2の_expect=入力をそのまま返す`() {
        val linear = LinearD2(outputX = 2, outputY = 2)

        // [[1, 2], [3, 4]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val context = Context(input)

        val result = linear._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = input, actual = result)
    }

    @Test
    fun `LinearD2の_train=deltaをそのまま返す`() {
        val linear = LinearD2(outputX = 2, outputY = 2)

        // [[1, 2], [3, 4]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 2) { x, y -> ((x * 2 + y) + 1) * 2.0f })
        }

        val result = linear._train(input, context, calcDelta) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        // deltaは[[2, 4], [6, 8]]
        assertEquals(expected = 2.0f, actual = dx[0, 0])
        assertEquals(expected = 4.0f, actual = dx[0, 1])
        assertEquals(expected = 6.0f, actual = dx[1, 0])
        assertEquals(expected = 8.0f, actual = dx[1, 1])
    }
}
