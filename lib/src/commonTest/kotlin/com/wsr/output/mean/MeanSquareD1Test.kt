@file:Suppress("NonAsciiCharacters")

package com.wsr.output.mean

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class MeanSquareD1Test {
    @Test
    fun `MeanSquareD1の_expect=入力をそのまま返す`() {
        // [1, 2, 3]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
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
                IOType.d1(listOf(1.0, 2.0, 3.0)),
                IOType.d1(listOf(4.0, 5.0, 6.0)),
            )
        // [[0, 1, 2], [3, 4, 5]]
        val label =
            listOf(
                IOType.d1(listOf(0.0, 1.0, 2.0)),
                IOType.d1(listOf(3.0, 4.0, 5.0)),
            )
        val meanSquare = MeanSquareD1(outputSize = 3)
        // [[1-0, 2-1, 3-2], [4-3, 5-4, 6-5]] = [[1, 1, 1], [1, 1, 1]]
        val result = meanSquare._train(input, label)

        assertEquals(expected = 2, actual = result.size)
        assertEquals(expected = IOType.d1(listOf(1.0, 1.0, 1.0)), actual = result[0])
        assertEquals(expected = IOType.d1(listOf(1.0, 1.0, 1.0)), actual = result[1])
    }
}
