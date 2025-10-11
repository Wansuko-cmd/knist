@file:Suppress("NonAsciiCharacters")

package com.wsr.reshape

import com.wsr.IOType
import com.wsr.reshape.reshape.ReshapeD2ToD1
import kotlin.test.Test
import kotlin.test.assertEquals

class ReshapeD2ToD1Test {
    @Test
    fun `ReshapeD2ToD1の_expect=D2をD1に平坦化`() {
        val reshape = ReshapeD2ToD1(outputSize = 4)

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
            )

        val result = reshape._expect(input)

        // [1, 2, 3, 4]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1
        assertEquals(expected = 1.0, actual = output[0])
        assertEquals(expected = 2.0, actual = output[1])
        assertEquals(expected = 3.0, actual = output[2])
        assertEquals(expected = 4.0, actual = output[3])
    }

    @Test
    fun `ReshapeD2ToD1の_train=D1のdeltaをD2に戻す`() {
        val reshape = ReshapeD2ToD1(outputSize = 4)

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
            )

        // deltaは[2, 4, 6, 8]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(2.0, 4.0, 6.0, 8.0)))
        }

        val result = reshape._train(input, calcDelta)

        // [[2, 4], [6, 8]]
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2
        assertEquals(expected = 2.0, actual = dx[0, 0])
        assertEquals(expected = 4.0, actual = dx[0, 1])
        assertEquals(expected = 6.0, actual = dx[1, 0])
        assertEquals(expected = 8.0, actual = dx[1, 1])
    }
}
