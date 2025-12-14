@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.process.compute.function.relu

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.process.Context
import kotlin.test.Test
import kotlin.test.assertEquals

class ReLUD2Test {
    @Test
    fun `ReLUD2の_expect=負の値を0にする`() {
        val relu = ReLUD2(outputX = 2, outputY = 2)

        // [[-2, -1], [0, 1]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y ->
                    when {
                        x == 0 && y == 0 -> -2.0f
                        x == 0 && y == 1 -> -1.0f
                        x == 1 && y == 0 -> 0.0f
                        else -> 1.0f
                    }
                },
            )
        val context = Context(input)

        // [[0, 0], [0, 1]]
        val result = relu._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 0.0f, actual = output[0, 0])
        assertEquals(expected = 0.0f, actual = output[0, 1])
        assertEquals(expected = 0.0f, actual = output[1, 0])
        assertEquals(expected = 1.0f, actual = output[1, 1])
    }

    @Test
    fun `ReLUD2の_train=入力が負の位置のdeltaを0にする`() {
        val relu = ReLUD2(outputX = 2, outputY = 2)

        // [[-1, 0], [1, 2]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y - 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[2, 3], [4, 5]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 2) { x, y -> (x * 2 + y + 2).toFloat() })
        }

        val result = relu._train(input, context, calcDelta) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        // input[[-1, 0], [1, 2]]なので、[-1]の位置は0、[0, 1, 2]の位置はdeltaをそのまま返す
        assertEquals(expected = 0.0f, actual = dx[0, 0])
        assertEquals(expected = 3.0f, actual = dx[0, 1])
        assertEquals(expected = 4.0f, actual = dx[1, 0])
        assertEquals(expected = 5.0f, actual = dx[1, 1])
    }
}
