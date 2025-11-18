@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.output.mean

import com.wsr.IOType
import com.wsr.output.mean.MeanSquareD1
import kotlin.test.Test
import kotlin.test.assertEquals

class MeanSquareD1Test {
    @Test
    fun `MeanSquareD1の_expect=入力をそのまま返す`() {
        // [1, 2, 3]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        val meanSquare = MeanSquareD1(outputSize = 3)
        val result = meanSquare._expect(input)

        assertEquals(expected = input, actual = result)
    }

    @Test
    fun `MeanSquareD1の_train=入力からラベルを引いた値を返す`() {
        // [[1, 2, 3], [4, 5, 6]]
        val input =
            listOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
                IOType.d1(listOf(4.0f, 5.0f, 6.0f)),
            )
        // [[0, 1, 2], [3, 4, 5]]
        val label =
            listOf(
                IOType.d1(listOf(0.0f, 1.0f, 2.0f)),
                IOType.d1(listOf(3.0f, 4.0f, 5.0f)),
            )
        val meanSquare = MeanSquareD1(outputSize = 3)
        // [[1-0, 2-1, 3-2], [4-3, 5-4, 6-5]] = [[1, 1, 1], [1, 1, 1]]
        val result = meanSquare._train(input, label)

        assertEquals(expected = 2, actual = result.size)
        assertEquals(expected = IOType.d1(listOf(1.0f, 1.0f, 1.0f)), actual = result[0])
        assertEquals(expected = IOType.d1(listOf(1.0f, 1.0f, 1.0f)), actual = result[1])
    }
}
