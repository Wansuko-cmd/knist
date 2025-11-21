@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.reshape.gap

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.reshape.gad.GlobalAverageD2ToD1
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalAverageD2ToD1Test {
    @Test
    fun `GlobalAverageD2ToD1の_expect=各行の平均を取りD1にする`() {
        val reshape = GlobalAverageD2ToD1(inputX = 2, inputY = 3)

        // [[1, 2, 3], [4, 5, 6]]
        val input =
            batchOf(
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() },
            )
        val context = Context(input)

        val result = reshape._expect(input, context) as Batch<IOType.D1>
        // [average([1, 2, 3]), average([4, 5, 6])] = [2.0f, 5.0f]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 2.0f, actual = output[0])
        assertEquals(expected = 5.0f, actual = output[1])
    }

    @Test
    fun `GlobalAverageD2ToD1の_train=deltaを各行に均等分配してD2にする`() {
        val reshape = GlobalAverageD2ToD1(inputX = 2, inputY = 3)

        // [[1, 2, 3], [4, 5, 6]]
        val input =
            batchOf(
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[6, 12]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(6.0f, 12.0f)))
        }

        val result = reshape._train(input, context, calcDelta) as Batch<IOType.D2>
        // delta / inputY = [6/3, 12/3] = [2, 4]
        // [[2, 2, 2], [4, 4, 4]]
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = 2.0f, actual = dx[0, 0])
        assertEquals(expected = 2.0f, actual = dx[0, 1])
        assertEquals(expected = 2.0f, actual = dx[0, 2])
        assertEquals(expected = 4.0f, actual = dx[1, 0])
        assertEquals(expected = 4.0f, actual = dx[1, 1])
        assertEquals(expected = 4.0f, actual = dx[1, 2])
    }
}
