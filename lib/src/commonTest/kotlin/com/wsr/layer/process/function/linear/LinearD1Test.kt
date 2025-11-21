@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.linear

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.get
import com.wsr.layer.Context
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearD1Test {
    @Test
    fun `LinearD1の_expect=入力をそのまま返す`() {
        val linear = LinearD1(outputSize = 3)

        // [[1, 2, 3]]
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        val context = Context(input)

        val result = linear._expect(input, context) as Batch<IOType.D1>
        assertEquals(expected = input, actual = result)
    }

    @Test
    fun `LinearD1の_train=deltaをそのまま返す`() {
        val linear = LinearD1(outputSize = 3)

        // [[1, 2, 3]]
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        val context = Context(input)

        // deltaは[2, 4, 6]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(2.0f, 4.0f, 6.0f)))
        }

        val result = linear._train(input, context, calcDelta) as Batch<IOType.D1>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D1
        assertEquals(expected = 2.0f, actual = dx[0])
        assertEquals(expected = 4.0f, actual = dx[1])
        assertEquals(expected = 6.0f, actual = dx[2])
    }
}
