@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.output.mean

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.core.set
import com.wsr.output.mean.MeanSquareD1
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.get

class MeanSquareD1Test {
    @Test
    fun `MeanSquareD1の_expect=入力をそのまま返す`() {
        // [1, 2, 3]
        val input =
            batchOf(
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
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
                IOType.d1(listOf(4.0f, 5.0f, 6.0f)),
            )
        // [[0, 1, 2], [3, 4, 5]]
        val label =
            batchOf(
                IOType.d1(listOf(0.0f, 1.0f, 2.0f)),
                IOType.d1(listOf(3.0f, 4.0f, 5.0f)),
            )
        val meanSquare = MeanSquareD1(outputSize = 3)
        // [[1-0, 2-1, 3-2], [4-3, 5-4, 6-5]] = [[1, 1, 1], [1, 1, 1]]
        val result = meanSquare._train(input, label)

        // loss = 0.5 * mean(delta^2) = 0.5 * mean([1^2, 1^2, 1^2, 1^2, 1^2, 1^2]) = 0.5 * 1.0 = 0.5
        assertEquals(expected = 0.5f, actual = result.loss)
        assertEquals(expected = 2, actual = result.delta.size)
        assertEquals(expected = IOType.d1(listOf(1.0f, 1.0f, 1.0f)), actual = (result.delta as Batch<IOType.D1>)[0])
        assertEquals(expected = IOType.d1(listOf(1.0f, 1.0f, 1.0f)), actual = result.delta[1])
    }
}
