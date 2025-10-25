@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.relu

import com.wsr.IOType
import com.wsr.layer.process.function.relu.ReLUD1
import kotlin.test.Test
import kotlin.test.assertEquals

class ReLUD1Test {
    @Test
    fun `ReLUD1の_expect=負の値を0にする`() {
        val relu = ReLUD1(outputSize = 4)

        // [[-2, -1, 0, 1]]
        val input =
            listOf(
                IOType.d1(listOf(-2.0, -1.0, 0.0, 1.0)),
            )

        // [[0, 0, 0, 1]]
        val result = relu._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1
        assertEquals(expected = 0.0, actual = output[0])
        assertEquals(expected = 0.0, actual = output[1])
        assertEquals(expected = 0.0, actual = output[2])
        assertEquals(expected = 1.0, actual = output[3])
    }

    @Test
    fun `ReLUD1の_train=入力が負の位置のdeltaを0にする`() {
        val relu = ReLUD1(outputSize = 3)

        // [[-1, 0, 1]]
        val input =
            listOf(
                IOType.d1(listOf(-1.0, 0.0, 1.0)),
            )

        // deltaは[2, 3, 4]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(2.0, 3.0, 4.0)))
        }

        val result = relu._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D1
        // input[-1, 0, 1]なので、[-1]の位置は0、[0, 1]の位置はdeltaをそのまま返す
        assertEquals(expected = 0.0, actual = dx[0])
        assertEquals(expected = 3.0, actual = dx[1])
        assertEquals(expected = 4.0, actual = dx[2])
    }
}
