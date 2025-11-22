@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.dropout

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.dropout.DropoutD3
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
class DropoutD3Test {
    @Test
    fun `DropoutD3の_expect=入力をそのまま返す`() {
        val dropout = DropoutD3(outputX = 2, outputY = 2, outputZ = 2, ratio = 0.5f, seed = 42)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        val result = dropout._expect(input, context) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        // Inverted Dropoutのexpect(推論時)では入力をそのまま返す
        assertEquals(expected = 1.0f, actual = output[0, 0, 0])
        assertEquals(expected = 2.0f, actual = output[0, 0, 1])
        assertEquals(expected = 3.0f, actual = output[0, 1, 0])
        assertEquals(expected = 4.0f, actual = output[0, 1, 1])
        assertEquals(expected = 5.0f, actual = output[1, 0, 0])
        assertEquals(expected = 6.0f, actual = output[1, 0, 1])
        assertEquals(expected = 7.0f, actual = output[1, 1, 0])
        assertEquals(expected = 8.0f, actual = output[1, 1, 1])
    }

    @Test
    fun `DropoutD3の_train=マスクを適用してdropout`() {
        val dropout = DropoutD3(outputX = 2, outputY = 2, outputZ = 2, ratio = 0.5f, seed = 42)

        // [[[1, 2], [3, 4]], [[5, 6], [7, 8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z -> (x * 4 + y * 2 + z + 1).toFloat() },
            )
        val context = Context(input)

        // 全て1のdelta
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d3(2, 2, 2) { _, _, _ -> 1.0f })
        }

        val result = dropout._train(input, context, calcDelta) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]

        // trainモードではランダムにマスク（0 or 1/ratio）が適用される
        // Inverted Dropoutでは、マスクは0または1/ratio (= 2.0f)
        val q = 1.0f / 0.5f // 2.0f
        var zeroCount = 0
        var qCount = 0
        for (x in 0 until 2) {
            for (y in 0 until 2) {
                for (z in 0 until 2) {
                    when (dx[x, y, z]) {
                        0.0f -> zeroCount++
                        q -> qCount++
                    }
                }
            }
        }

        // マスクは0かq(2.0f)のみ
        assertEquals(expected = 8, actual = zeroCount + qCount)
        // ratio=0.5なので、少なくとも1つは0とqがある
        assertTrue(zeroCount > 0)
        assertTrue(qCount > 0)
    }
}
