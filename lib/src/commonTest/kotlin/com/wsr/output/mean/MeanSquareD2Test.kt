@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.output.mean

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.d2
import com.wsr.get
import com.wsr.output.mean.MeanSquareD2
import com.wsr.set
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.get

class MeanSquareD2Test {
    @Test
    fun `MeanSquareD2の_expect=入力をそのまま返す`() {
        // [[1, 2], [3, 4]]
        val input =
            batchOf(
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
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        // [[0, 1], [2, 3]]
        val label =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y).toFloat() },
            )
        val meanSquare = MeanSquareD2(outputX = 2, outputY = 2)
        // [[1-0, 2-1], [3-2, 4-3]] = [[1, 1], [1, 1]]
        val result = meanSquare._train(input, label)

        // loss = 0.5 * mean(delta^2) = 0.5 * mean([1^2, 1^2, 1^2, 1^2]) = 0.5 * 1.0 = 0.5
        assertEquals(expected = 0.5f, actual = result.loss)
        assertEquals(expected = 1, actual = result.delta.size)
        val output = result.delta as Batch<IOType.D2>
        assertEquals(expected = 2, actual = output.shape[0])
        assertEquals(expected = 2, actual = output.shape[1])
        assertEquals(expected = 1.0f, actual = output[0][0, 0])
        assertEquals(expected = 1.0f, actual = output[0][0, 1])
        assertEquals(expected = 1.0f, actual = output[0][1, 0])
        assertEquals(expected = 1.0f, actual = output[0][1, 1])
    }
}
