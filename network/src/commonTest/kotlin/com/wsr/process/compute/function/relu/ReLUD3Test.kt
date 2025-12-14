@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.process.compute.function.relu

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.process.Context
import kotlin.test.Test
import kotlin.test.assertEquals

class ReLUD3Test {
    @Test
    fun `ReLUD3の_expect=負の値を0にする`() {
        val relu = ReLUD3(outputX = 2, outputY = 2, outputZ = 2)

        // [[[1, -2], [3, -4]], [[5, -6], [7, -8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z ->
                    val value = (x * 4 + y * 2 + z + 1).toFloat()
                    if (z % 2 == 1) -value else value
                },
            )
        val context = Context(input)

        val result = relu._expect(input, context) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        // 負の値は0、正の値はそのまま
        assertEquals(expected = 1.0f, actual = output[0, 0, 0]) // 1 -> 1
        assertEquals(expected = 0.0f, actual = output[0, 0, 1]) // -2 -> 0
        assertEquals(expected = 3.0f, actual = output[0, 1, 0]) // 3 -> 3
        assertEquals(expected = 0.0f, actual = output[0, 1, 1]) // -4 -> 0
        assertEquals(expected = 5.0f, actual = output[1, 0, 0]) // 5 -> 5
        assertEquals(expected = 0.0f, actual = output[1, 0, 1]) // -6 -> 0
        assertEquals(expected = 7.0f, actual = output[1, 1, 0]) // 7 -> 7
        assertEquals(expected = 0.0f, actual = output[1, 1, 1]) // -8 -> 0
    }

    @Test
    fun `ReLUD3の_train=入力が負の位置のdeltaを0にする`() {
        val relu = ReLUD3(outputX = 2, outputY = 2, outputZ = 2)

        // [[[1, -2], [3, -4]], [[5, -6], [7, -8]]]
        val input =
            batchOf(
                IOType.d3(2, 2, 2) { x, y, z ->
                    val value = (x * 4 + y * 2 + z + 1).toFloat()
                    if (z % 2 == 1) -value else value
                },
            )
        val context = Context(input)

        // 全て1のdelta
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d3(2, 2, 2) { _, _, _ -> 1.0f })
        }

        val result = relu._train(input, context, calcDelta) as Batch<IOType.D3>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        // 入力が負の位置は0、正の位置はdeltaをそのまま伝播
        assertEquals(expected = 1.0f, actual = dx[0, 0, 0]) // 入力1 -> 1
        assertEquals(expected = 0.0f, actual = dx[0, 0, 1]) // 入力-2 -> 0
        assertEquals(expected = 1.0f, actual = dx[0, 1, 0]) // 入力3 -> 1
        assertEquals(expected = 0.0f, actual = dx[0, 1, 1]) // 入力-4 -> 0
        assertEquals(expected = 1.0f, actual = dx[1, 0, 0]) // 入力5 -> 1
        assertEquals(expected = 0.0f, actual = dx[1, 0, 1]) // 入力-6 -> 0
        assertEquals(expected = 1.0f, actual = dx[1, 1, 0]) // 入力7 -> 1
        assertEquals(expected = 0.0f, actual = dx[1, 1, 1]) // 入力-8 -> 0
    }
}
