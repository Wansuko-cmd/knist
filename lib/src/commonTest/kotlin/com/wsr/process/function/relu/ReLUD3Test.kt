@file:Suppress("NonAsciiCharacters")

package com.wsr.process.function.relu

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class ReLUD3Test {
    @Test
    fun `ReLUD3の_expect=負の値を0にする`() {
        val relu = ReLUD3(outputX = 2, outputY = 2, outputZ = 2)

        // [[[1, -2], [3, -4]], [[5, -6], [7, -8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z ->
                    val value = (x * 4 + y * 2 + z + 1).toDouble()
                    if (z % 2 == 1) -value else value
                },
            )

        val result = relu._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D3
        // 負の値は0、正の値はそのまま
        assertEquals(expected = 1.0, actual = output[0, 0, 0]) // 1 -> 1
        assertEquals(expected = 0.0, actual = output[0, 0, 1]) // -2 -> 0
        assertEquals(expected = 3.0, actual = output[0, 1, 0]) // 3 -> 3
        assertEquals(expected = 0.0, actual = output[0, 1, 1]) // -4 -> 0
        assertEquals(expected = 5.0, actual = output[1, 0, 0]) // 5 -> 5
        assertEquals(expected = 0.0, actual = output[1, 0, 1]) // -6 -> 0
        assertEquals(expected = 7.0, actual = output[1, 1, 0]) // 7 -> 7
        assertEquals(expected = 0.0, actual = output[1, 1, 1]) // -8 -> 0
    }

    @Test
    fun `ReLUD3の_train=入力が負の位置のdeltaを0にする`() {
        val relu = ReLUD3(outputX = 2, outputY = 2, outputZ = 2)

        // [[[1, -2], [3, -4]], [[5, -6], [7, -8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z ->
                    val value = (x * 4 + y * 2 + z + 1).toDouble()
                    if (z % 2 == 1) -value else value
                },
            )

        // 全て1のdelta
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d3(2, 2, 2) { _, _, _ -> 1.0 })
        }

        val result = relu._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D3
        // 入力が負の位置は0、正の位置はdeltaをそのまま伝播
        assertEquals(expected = 1.0, actual = dx[0, 0, 0]) // 入力1 -> 1
        assertEquals(expected = 0.0, actual = dx[0, 0, 1]) // 入力-2 -> 0
        assertEquals(expected = 1.0, actual = dx[0, 1, 0]) // 入力3 -> 1
        assertEquals(expected = 0.0, actual = dx[0, 1, 1]) // 入力-4 -> 0
        assertEquals(expected = 1.0, actual = dx[1, 0, 0]) // 入力5 -> 1
        assertEquals(expected = 0.0, actual = dx[1, 0, 1]) // 入力-6 -> 0
        assertEquals(expected = 1.0, actual = dx[1, 1, 0]) // 入力7 -> 1
        assertEquals(expected = 0.0, actual = dx[1, 1, 1]) // 入力-8 -> 0
    }
}
