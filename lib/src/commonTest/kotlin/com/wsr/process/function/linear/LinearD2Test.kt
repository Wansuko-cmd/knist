@file:Suppress("NonAsciiCharacters")

package com.wsr.process.function.linear

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearD2Test {
    @Test
    fun `LinearD2の_expect=入力をそのまま返す`() {
        val linear = LinearD2(outputX = 2, outputY = 2)

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
            )

        val result = linear._expect(input)

        assertEquals(expected = input, actual = result)
    }

    @Test
    fun `LinearD2の_train=deltaをそのまま返す`() {
        val linear = LinearD2(outputX = 2, outputY = 2)

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
            )

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 2) { x, y -> ((x * 2 + y) + 1) * 2.0 })
        }

        val result = linear._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2
        // deltaは[[2, 4], [6, 8]]
        assertEquals(expected = 2.0, actual = dx[0, 0])
        assertEquals(expected = 4.0, actual = dx[0, 1])
        assertEquals(expected = 6.0, actual = dx[1, 0])
        assertEquals(expected = 8.0, actual = dx[1, 1])
    }
}
