@file:Suppress("NonAsciiCharacters")

package com.wsr.output.mean

import com.wsr.IOType
import com.wsr.output.mean.MeanSquareD2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.get

class MeanSquareD2Test {
    @Test
    fun `MeanSquareD2の_expect=入力をそのまま返す`() {
        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val meanSquare = MeanSquareD2(outputX = 2, outputY = 2)
        val result = meanSquare._expect(input)

        assertEquals(expected = input, actual = result)
    }

    @Test
    fun `MeanSquareD2の_train=入力からラベルを引いた値を返す`() {
        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        // [[0, 1], [2, 3]]
        val label =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y).toFloat() },
            )
        val meanSquare = MeanSquareD2(outputX = 2, outputY = 2)
        // [[1-0, 2-1], [3-2, 4-3]] = [[1, 1], [1, 1]]
        val result = meanSquare._train(input, label)

        assertEquals(expected = 1, actual = result.delta.size)
        val output = result.delta[0] as IOType.D2
        assertEquals(expected = 2, actual = output.shape[0])
        assertEquals(expected = 2, actual = output.shape[1])
        assertEquals(expected = 1.0f, actual = output[0, 0])
        assertEquals(expected = 1.0f, actual = output[0, 1])
        assertEquals(expected = 1.0f, actual = output[1, 0])
        assertEquals(expected = 1.0f, actual = output[1, 1])
    }
}
