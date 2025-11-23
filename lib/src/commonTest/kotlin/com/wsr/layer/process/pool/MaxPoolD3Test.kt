@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.pool

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.set
import com.wsr.layer.Context
import com.wsr.layer.process.pool.MaxPoolD3
import kotlin.test.Test
import kotlin.test.assertEquals

class MaxPoolD3Test {
    @Test
    fun `MaxPoolD3の_expect=poolSize毎の最大値を取る`() {
        val maxPool = MaxPoolD3(poolSize = 2, channel = 1, inputX = 4, inputY = 4)

        // 1チャネル、4x4の入力
        // [[[1, 2, 3, 4],
        //   [5, 6, 7, 8],
        //   [9, 10, 11, 12],
        //   [13, 14, 15, 16]]]
        val input =
            batchOf(
                IOType.d3(1, 4, 4) { _, y, z -> (y * 4 + z + 1).toFloat() },
            )
        val context = Context(input)

        val result = maxPool._expect(input, context) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]

        // outputサイズは (1, 2, 2)
        assertEquals(expected = 1, actual = output.shape[0])
        assertEquals(expected = 2, actual = output.shape[1])
        assertEquals(expected = 2, actual = output.shape[2])

        // forwardは output[x, y, z] = max(input[x, y+i, z+j]) for i,j in [0, poolSize)
        // 注意: これは重複のあるプーリング（overlapping pooling）
        // output[0, 0, 0] = max(input[0, 0+i, 0+j]) = max(1, 2, 5, 6) = 6
        // output[0, 0, 1] = max(input[0, 0+i, 1+j]) = max(2, 3, 6, 7) = 7
        // output[0, 1, 0] = max(input[0, 1+i, 0+j]) = max(5, 6, 9, 10) = 10
        // output[0, 1, 1] = max(input[0, 1+i, 1+j]) = max(6, 7, 10, 11) = 11
        assertEquals(expected = 6.0f, actual = output[0, 0, 0])
        assertEquals(expected = 7.0f, actual = output[0, 0, 1])
        assertEquals(expected = 10.0f, actual = output[0, 1, 0])
        assertEquals(expected = 11.0f, actual = output[0, 1, 1])
    }

    @Test
    fun `MaxPoolD3の_train=最大値の位置にdeltaを伝播`() {
        val maxPool = MaxPoolD3(poolSize = 2, channel = 1, inputX = 4, inputY = 4)

        // 1チャネル、4x4の入力
        val input =
            batchOf(
                IOType.d3(1, 4, 4) { _, y, z -> (y * 4 + z + 1).toFloat() },
            )
        val context = Context(input)

        // 全て1のdelta (2x2)
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d3(1, 2, 2) { _, _, _ -> 1.0f })
        }

        val result = maxPool._train(input, context, calcDelta) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]

        // 入力サイズは4x4
        assertEquals(expected = 1, actual = dx.shape[0])
        assertEquals(expected = 4, actual = dx.shape[1])
        assertEquals(expected = 4, actual = dx.shape[2])

        // 最大値の位置にのみdeltaが伝播
        // overlapping poolingなので、各outputの最大値位置を確認:
        // output[0, 0, 0] = max(1, 2, 5, 6) → 位置(1,1)の値6
        // output[0, 0, 1] = max(2, 3, 6, 7) → 位置(1,2)の値7
        // output[0, 1, 0] = max(5, 6, 9, 10) → 位置(2,1)の値10
        // output[0, 1, 1] = max(6, 7, 10, 11) → 位置(2,2)の値11

        // 行0
        assertEquals(expected = 0.0f, actual = dx[0, 0, 0]) // 1
        assertEquals(expected = 0.0f, actual = dx[0, 0, 1]) // 2
        assertEquals(expected = 0.0f, actual = dx[0, 0, 2]) // 3
        assertEquals(expected = 0.0f, actual = dx[0, 0, 3]) // 4

        // 行1
        assertEquals(expected = 0.0f, actual = dx[0, 1, 0]) // 5
        assertEquals(expected = 1.0f, actual = dx[0, 1, 1]) // 6 (max for output[0,0,0])
        assertEquals(expected = 1.0f, actual = dx[0, 1, 2]) // 7 (max for output[0,0,1])
        assertEquals(expected = 0.0f, actual = dx[0, 1, 3]) // 8

        // 行2
        assertEquals(expected = 0.0f, actual = dx[0, 2, 0]) // 9
        assertEquals(expected = 1.0f, actual = dx[0, 2, 1]) // 10 (max for output[0,1,0])
        assertEquals(expected = 1.0f, actual = dx[0, 2, 2]) // 11 (max for output[0,1,1])
        assertEquals(expected = 0.0f, actual = dx[0, 2, 3]) // 12

        // 行3
        assertEquals(expected = 0.0f, actual = dx[0, 3, 0]) // 13
        assertEquals(expected = 0.0f, actual = dx[0, 3, 1]) // 14
        assertEquals(expected = 0.0f, actual = dx[0, 3, 2]) // 15
        assertEquals(expected = 0.0f, actual = dx[0, 3, 3]) // 16
    }
}
