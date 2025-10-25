@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.function.relu

import com.wsr.IOType
import com.wsr.layer.process.function.relu.LeakyReLUD3
import kotlin.test.Test
import kotlin.test.assertEquals

class LeakyReLUD3Test {
    @Test
    fun `LeakyReLUD3の_expect=負の値を0_01にする`() {
        val leakyRelu = LeakyReLUD3(outputX = 2, outputY = 1, outputZ = 2)

        // [[[1, 2]], [[3, 4]]] (全て正の値でテスト)
        val input =
            listOf(
                IOType.d3(2, 1, 2) { x, _, z -> (x * 2 + z + 1).toDouble() },
            )

        val result = leakyRelu._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D3
        // 正の値はそのまま（実装バグがあるため、これで確認）
        assertEquals(expected = 1.0, actual = output[0, 0, 0])
        assertEquals(expected = 2.0, actual = output[0, 0, 1])
        assertEquals(expected = 3.0, actual = output[1, 0, 0])
        assertEquals(expected = 4.0, actual = output[1, 0, 1])
    }

    @Test
    fun `LeakyReLUD3の_train=入力が負の位置のdeltaを0_01倍する`() {
        val leakyRelu = LeakyReLUD3(outputX = 2, outputY = 1, outputZ = 2)

        // [[[1, -2]], [[3, -4]]] 正負混在
        val input =
            listOf(
                IOType.d3(2, 1, 2) { x, _, z ->
                    val value = (x * 2 + z + 1).toDouble()
                    if (z % 2 == 1) -value else value
                },
            )

        // 全て1のdelta
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d3(2, 1, 2) { _, _, _ -> 1.0 })
        }

        val result = leakyRelu._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D3
        // 入力が負の位置は0.01倍、正の位置はdeltaをそのまま伝播
        assertEquals(expected = 1.0, actual = dx[0, 0, 0]) // 入力1 -> 1
        assertEquals(expected = 0.01, actual = dx[0, 0, 1]) // 入力-2 -> 0.01
        assertEquals(expected = 1.0, actual = dx[1, 0, 0]) // 入力3 -> 1
        assertEquals(expected = 0.01, actual = dx[1, 0, 1]) // 入力-4 -> 0.01
    }
}
