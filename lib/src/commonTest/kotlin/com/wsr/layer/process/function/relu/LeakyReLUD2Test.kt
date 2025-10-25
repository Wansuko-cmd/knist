@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.relu

import com.wsr.IOType
import com.wsr.layer.process.function.relu.LeakyReLUD2
import kotlin.test.Test
import kotlin.test.assertEquals

class LeakyReLUD2Test {
    @Test
    fun `LeakyReLUD2の_expect=負の値を0_01にする`() {
        val leakyRelu = LeakyReLUD2(outputX = 2, outputY = 2)

        // [[-2, -1], [0, 1]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y ->
                    when {
                        x == 0 && y == 0 -> -2.0
                        x == 0 && y == 1 -> -1.0
                        x == 1 && y == 0 -> 0.0
                        else -> 1.0
                    }
                },
            )

        // [[0.01, 0.01], [0, 1]]
        val result = leakyRelu._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = 0.01, actual = output[0, 0])
        assertEquals(expected = 0.01, actual = output[0, 1])
        assertEquals(expected = 0.0, actual = output[1, 0])
        assertEquals(expected = 1.0, actual = output[1, 1])
    }

    @Test
    fun `LeakyReLUD2の_train=入力が負の位置のdeltaを0_01倍する`() {
        val leakyRelu = LeakyReLUD2(outputX = 2, outputY = 2)

        // [[-1, 0], [1, 2]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y - 1).toDouble() },
            )

        // deltaは[[2, 3], [4, 5]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 2) { x, y -> (x * 2 + y + 2).toDouble() })
        }

        val result = leakyRelu._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2
        // input[[-1, 0], [1, 2]]なので、[-1]の位置は0.01倍、[0, 1, 2]の位置はdeltaをそのまま返す
        assertEquals(expected = 0.02, actual = dx[0, 0])
        assertEquals(expected = 3.0, actual = dx[0, 1])
        assertEquals(expected = 4.0, actual = dx[1, 0])
        assertEquals(expected = 5.0, actual = dx[1, 1])
    }
}
