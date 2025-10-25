@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.reshape.gap

import com.wsr.IOType
import com.wsr.layer.reshape.gad.GlobalAverageD3ToD1
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalAverageD3ToD1Test {
    @Test
    fun `GlobalAverageD3ToD1の_expect=各チャネルの平均を取りD1にする`() {
        val reshape = GlobalAverageD3ToD1(inputX = 2, inputY = 2, inputZ = 2)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
            )

        val result = reshape._expect(input)

        // [average([1, 2, 3, 4]), average([5, 6, 7, 8])] = [2.5, 6.5]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1
        assertEquals(expected = 2.5, actual = output[0])
        assertEquals(expected = 6.5, actual = output[1])
    }

    @Test
    fun `GlobalAverageD3ToD1の_train=deltaを各チャネルに均等分配してD3にする`() {
        val reshape = GlobalAverageD3ToD1(inputX = 2, inputY = 2, inputZ = 2)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toDouble() },
            )

        // deltaは[8, 16]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(8.0, 16.0)))
        }

        val result = reshape._train(input, calcDelta)

        // delta / (inputY * inputZ) = [8/(2*2), 16/(2*2)] = [2, 4]
        // [[[2, 2], [2, 2]], [[4, 4], [4, 4]]]
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D3
        assertEquals(expected = 2.0, actual = dx[0, 0, 0])
        assertEquals(expected = 2.0, actual = dx[0, 0, 1])
        assertEquals(expected = 2.0, actual = dx[0, 1, 0])
        assertEquals(expected = 2.0, actual = dx[0, 1, 1])
        assertEquals(expected = 4.0, actual = dx[1, 0, 0])
        assertEquals(expected = 4.0, actual = dx[1, 0, 1])
        assertEquals(expected = 4.0, actual = dx[1, 1, 0])
        assertEquals(expected = 4.0, actual = dx[1, 1, 1])
    }
}
