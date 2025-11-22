@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.reshape.gap

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.reshape.gad.GlobalAverageD3ToD2
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalAverageD3ToD2Test {
    @Test
    fun `GlobalAverageD3ToD2の_expect=各位置の平均を取りD2にする`() {
        val reshape = GlobalAverageD3ToD2(inputX = 2, inputY = 2, inputZ = 2)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        val result = reshape._expect(input, context) as Batch<IOType.D2>
        // transpose(2,0,1)で[[[1,3],[5,7]], [[2,4],[6,8]]]になり、各(x,z)位置でyの平均を取る
        // output[0,0] = average([1, 3]) = 2.0f
        // output[0,1] = average([5, 7]) = 6.0f
        // output[1,0] = average([2, 4]) = 3.0f
        // output[1,1] = average([6, 8]) = 7.0f
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 2.0f, actual = output[0, 0])
        assertEquals(expected = 6.0f, actual = output[0, 1])
        assertEquals(expected = 3.0f, actual = output[1, 0])
        assertEquals(expected = 7.0f, actual = output[1, 1])
    }

    @Test
    fun `GlobalAverageD3ToD2の_train=deltaを各チャネルに均等分配してD3にする`() {
        val reshape = GlobalAverageD3ToD2(inputX = 2, inputY = 2, inputZ = 2)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 2) { y, z -> ((y * 2 + z) + 1) * 2.0f })
        }

        val result = reshape._train(input, context, calcDelta) as Batch<IOType.D3>
        // delta / inputX = [[2/2, 4/2], [6/2, 8/2]] = [[1, 2], [3, 4]]
        // 各チャネルで同じ値が分配される
        // [[[1, 2], [3, 4]], [[1, 2], [3, 4]]]
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = 1.0f, actual = dx[0, 0, 0])
        assertEquals(expected = 2.0f, actual = dx[0, 0, 1])
        assertEquals(expected = 3.0f, actual = dx[0, 1, 0])
        assertEquals(expected = 4.0f, actual = dx[0, 1, 1])
        assertEquals(expected = 1.0f, actual = dx[1, 0, 0])
        assertEquals(expected = 2.0f, actual = dx[1, 0, 1])
        assertEquals(expected = 3.0f, actual = dx[1, 1, 0])
        assertEquals(expected = 4.0f, actual = dx[1, 1, 1])
    }
}
