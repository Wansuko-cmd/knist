@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.dropout

import com.wsr.IOType
import com.wsr.layer.process.dropout.DropoutD3
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DropoutD3Test {
    @Test
    fun `DropoutD3の_expect=入力をそのまま返す`() {
        val dropout = DropoutD3(outputX = 2, outputY = 2, outputZ = 2, ratio = 0.5, seed = 42)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )

        val result = dropout._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D3
        // Inverted Dropoutのexpect(推論時)では入力をそのまま返す
        assertEquals(expected = 1.0, actual = output[0, 0, 0])
        assertEquals(expected = 2.0, actual = output[0, 0, 1])
        assertEquals(expected = 3.0, actual = output[0, 1, 0])
        assertEquals(expected = 4.0, actual = output[0, 1, 1])
        assertEquals(expected = 5.0, actual = output[1, 0, 0])
        assertEquals(expected = 6.0, actual = output[1, 0, 1])
        assertEquals(expected = 7.0, actual = output[1, 1, 0])
        assertEquals(expected = 8.0, actual = output[1, 1, 1])
    }

    @Test
    fun `DropoutD3の_train=マスクを適用してdropout`() {
        val dropout = DropoutD3(outputX = 2, outputY = 2, outputZ = 2, ratio = 0.5, seed = 42)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            listOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )

        // 全て1のdelta
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d3(2, 2, 2) { _, _, _ -> 1.0 })
        }

        val result = dropout._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D3

        // trainモードではランダムにマスク（0 or 1/ratio）が適用される
        // Inverted Dropoutでは、マスクは0または1/ratio (= 2.0)
        val q = 1.0 / 0.5 // 2.0
        var zeroCount = 0
        var qCount = 0
        for (x in 0 until 2) {
            for (y in 0 until 2) {
                for (z in 0 until 2) {
                    when (dx[x, y, z]) {
                        0.0 -> zeroCount++
                        q -> qCount++
                    }
                }
            }
        }

        // マスクは0かq(2.0)のみ
        assertEquals(expected = 8, actual = zeroCount + qCount)
        // ratio=0.5なので、少なくとも1つは0とqがある
        assertTrue(zeroCount > 0)
        assertTrue(qCount > 0)
    }
}
