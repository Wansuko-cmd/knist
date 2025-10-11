@file:Suppress("NonAsciiCharacters")

package com.wsr.reshape

import com.wsr.IOType
import com.wsr.reshape.gad.GlobalAverageD2ToD1
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalAverageD2ToD1Test {
    @Test
    fun `GlobalAverageD2ToD1の_expect=各行の平均を取りD1にする`() {
        val reshape = GlobalAverageD2ToD1(inputX = 2, inputY = 3)

        // [[1, 2, 3], [4, 5, 6]]
        val input =
            listOf(
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toDouble() },
            )

        val result = reshape._expect(input)

        // [average([1, 2, 3]), average([4, 5, 6])] = [2.0, 5.0]
        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1
        assertEquals(expected = 2.0, actual = output[0])
        assertEquals(expected = 5.0, actual = output[1])
    }

    @Test
    fun `GlobalAverageD2ToD1の_train=deltaを各行に均等分配してD2にする`() {
        val reshape = GlobalAverageD2ToD1(inputX = 2, inputY = 3)

        // [[1, 2, 3], [4, 5, 6]]
        val input =
            listOf(
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toDouble() },
            )

        // deltaは[6, 12]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(6.0, 12.0)))
        }

        val result = reshape._train(input, calcDelta)

        // delta / inputY = [6/3, 12/3] = [2, 4]
        // [[2, 2, 2], [4, 4, 4]]
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2
        assertEquals(expected = 2.0, actual = dx[0, 0])
        assertEquals(expected = 2.0, actual = dx[0, 1])
        assertEquals(expected = 2.0, actual = dx[0, 2])
        assertEquals(expected = 4.0, actual = dx[1, 0])
        assertEquals(expected = 4.0, actual = dx[1, 1])
        assertEquals(expected = 4.0, actual = dx[1, 2])
    }
}
