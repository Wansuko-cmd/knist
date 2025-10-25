@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.linear

import com.wsr.IOType
import com.wsr.layer.process.function.linear.LinearD1
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearD1Test {
    @Test
    fun `LinearD1の_expect=入力をそのまま返す`() {
        val linear = LinearD1(outputSize = 3)

        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
            )

        val result = linear._expect(input)

        assertEquals(expected = input, actual = result)
    }

    @Test
    fun `LinearD1の_train=deltaをそのまま返す`() {
        val linear = LinearD1(outputSize = 3)

        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
            )

        // deltaは[2, 4, 6]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(2.0, 4.0, 6.0)))
        }

        val result = linear._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D1
        assertEquals(expected = 2.0, actual = dx[0])
        assertEquals(expected = 4.0, actual = dx[1])
        assertEquals(expected = 6.0, actual = dx[2])
    }
}
