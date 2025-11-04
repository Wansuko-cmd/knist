@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.output.mean

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class MeanSquareD2Test {
    @Test
    fun `MeanSquareD2の_expect=入力をそのまま返す`() {
        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
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
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
            )
        // [[0, 1], [2, 3]]
        val label =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y).toDouble() },
            )
        val meanSquare = MeanSquareD2(outputX = 2, outputY = 2)
        // [[1-0, 2-1], [3-2, 4-3]] = [[1, 1], [1, 1]]
        val result = meanSquare._train(input, label)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = 2, actual = output.shape[0])
        assertEquals(expected = 2, actual = output.shape[1])
        assertEquals(expected = 1.0, actual = output[0, 0])
        assertEquals(expected = 1.0, actual = output[0, 1])
        assertEquals(expected = 1.0, actual = output[1, 0])
        assertEquals(expected = 1.0, actual = output[1, 1])
    }

    @Test
    fun `MeanSquareD2の_train=複数バッチでも正しく動作する`() {
        // バッチ1: [[1, 2, 3], [4, 5, 6]]
        // バッチ2: [[7, 8, 9], [10, 11, 12]]
        val input =
            listOf(
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toDouble() },
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 7).toDouble() },
            )
        // バッチ1のラベル: [[0, 1, 2], [3, 4, 5]]
        // バッチ2のラベル: [[6, 7, 8], [9, 10, 11]]
        val label =
            listOf(
                IOType.d2(2, 3) { x, y -> (x * 3 + y).toDouble() },
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 6).toDouble() },
            )
        val meanSquare = MeanSquareD2(outputX = 2, outputY = 3)
        val result = meanSquare._train(input, label)

        assertEquals(expected = 2, actual = result.size)

        // バッチ1の出力: 全要素が1
        val output1 = result[0] as IOType.D2
        assertEquals(expected = 2, actual = output1.shape[0])
        assertEquals(expected = 3, actual = output1.shape[1])
        for (x in 0 until 2) {
            for (y in 0 until 3) {
                assertEquals(expected = 1.0, actual = output1[x, y], message = "output1[$x,$y]")
            }
        }

        // バッチ2の出力: 全要素が1
        val output2 = result[1] as IOType.D2
        assertEquals(expected = 2, actual = output2.shape[0])
        assertEquals(expected = 3, actual = output2.shape[1])
        for (x in 0 until 2) {
            for (y in 0 until 3) {
                assertEquals(expected = 1.0, actual = output2[x, y], message = "output2[$x,$y]")
            }
        }
    }

    @Test
    fun `MeanSquareD2の_train=負の値も正しく扱える`() {
        // [[5, 10]]
        val input =
            listOf(
                IOType.d2(1, 2) { _, y -> ((y + 1) * 5).toDouble() },
            )
        // [[7, 8]]
        val label =
            listOf(
                IOType.d2(1, 2) { _, y -> (y + 7).toDouble() },
            )
        val meanSquare = MeanSquareD2(outputX = 1, outputY = 2)
        // [[5-7, 10-8]] = [[-2, 2]]
        val result = meanSquare._train(input, label)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = -2.0, actual = output[0, 0])
        assertEquals(expected = 2.0, actual = output[0, 1])
    }
}
