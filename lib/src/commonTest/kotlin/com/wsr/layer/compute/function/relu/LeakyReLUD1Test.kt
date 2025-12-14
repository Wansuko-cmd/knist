@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.compute.function.relu

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.layer.Context
import kotlin.test.Test
import kotlin.test.assertEquals

class LeakyReLUD1Test {
    @Test
    fun `LeakyReLUD1の_expect=負の値を0_01にする`() {
        val leakyRelu = LeakyReLUD1(outputSize = 4)

        // [[-2, -1, 0, 1]]
        val input =
            batchOf(
                IOType.d1(listOf(-2.0f, -1.0f, 0.0f, 1.0f)),
            )
        val context = Context(input)

        // [[0.01f, 0.01f, 0, 1]]
        val result = leakyRelu._expect(input, context) as Batch<IOType.D1>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 0.01f, actual = output[0])
        assertEquals(expected = 0.01f, actual = output[1])
        assertEquals(expected = 0.0f, actual = output[2])
        assertEquals(expected = 1.0f, actual = output[3])
    }

    @Test
    fun `LeakyReLUD1の_train=入力が負の位置のdeltaを0_01倍する`() {
        val leakyRelu = LeakyReLUD1(outputSize = 3)

        // [[-1, 0, 1]]
        val input =
            batchOf(
                IOType.d1(listOf(-1.0f, 0.0f, 1.0f)),
            )
        val context = Context(input)

        // deltaは[2, 3, 4]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(2.0f, 3.0f, 4.0f)))
        }

        val result = leakyRelu._train(input, context, calcDelta) as Batch<IOType.D1>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        // input[-1, 0, 1]なので、[-1]の位置は0.01倍、[0, 1]の位置はdeltaをそのまま返す
        assertEquals(expected = 0.02f, actual = dx[0])
        assertEquals(expected = 3.0f, actual = dx[1])
        assertEquals(expected = 4.0f, actual = dx[2])
    }
}
