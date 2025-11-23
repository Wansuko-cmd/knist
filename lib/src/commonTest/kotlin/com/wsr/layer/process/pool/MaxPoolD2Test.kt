@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.pool

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.layer.Context
import com.wsr.layer.process.pool.MaxPoolD2
import com.wsr.core.set
import kotlin.test.Test
import kotlin.test.assertEquals

class MaxPoolD2Test {
    @Test
    fun `MaxPoolD2の_expect=poolSize毎の最大値を取る`() {
        val maxPool =
            MaxPoolD2(
                poolSize = 2,
                channel = 1,
                inputSize = 4,
            )

        // [[1, 2, 3, 4]]
        val input =
            batchOf(
                IOType.d2(1, 4) { _, y -> (y + 1).toFloat() },
            )
        val context = Context(input)

        // forward実装: IOType.d2(outputX, outputY) { x, y -> ... input[x, y] ... }
        // outputY = inputSize / poolSize = 4 / 2 = 2
        // y=0のとき: max(input[0,0], input[0,1]) = max(1, 2) = 2
        // y=1のとき: max(input[0,1], input[0,2]) = max(2, 3) = 3
        val result = maxPool._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 2.0f, actual = output[0, 0])
        assertEquals(expected = 3.0f, actual = output[0, 1])
    }

    @Test
    fun `MaxPoolD2の_train=最大値の位置にdeltaを伝播`() {
        val maxPool =
            MaxPoolD2(
                poolSize = 2,
                channel = 1,
                inputSize = 4,
            )

        // [[1, 3, 2, 4]]
        val input =
            batchOf(
                IOType.d2(1, 4) { _, y ->
                    when (y) {
                        0 -> 1.0f
                        1 -> 3.0f
                        2 -> 2.0f
                        else -> 4.0f
                    }
                },
            )
        val context = Context(input)

        // deltaは[[2, 6]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(1, 2) { _, y -> if (y == 0) 2.0f else 6.0f })
        }

        val result = maxPool._train(input, context, calcDelta) as Batch<IOType.D2>
        // forward: y=0でmax(input[0,0], input[0,1]) = max(1, 3) = 3
        //          y=1でmax(input[0,1], input[0,2]) = max(3, 2) = 3
        // trainの実装: o = i / poolSize
        //   i=0: o=0, input[0,0]=1 != output[0,0]=3 → 0
        //   i=1: o=0, input[0,1]=3 == output[0,0]=3 → delta[0,0]=2
        //   i=2: o=1, input[0,2]=2 != output[0,1]=3 → 0
        //   i=3: o=1, input[0,3]=4 != output[0,1]=3 → 0
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = 0.0f, actual = dx[0, 0])
        assertEquals(expected = 2.0f, actual = dx[0, 1])
        assertEquals(expected = 0.0f, actual = dx[0, 2])
        assertEquals(expected = 0.0f, actual = dx[0, 3])
    }
}
