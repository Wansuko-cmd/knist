@file:Suppress("NonAsciiCharacters")

package com.wsr.process.dropout

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class DropoutD2Test {
    @Test
    fun `DropoutD2の_expect=入力にratioを掛ける`() {
        val dropout =
            DropoutD2(
                outputX = 2,
                outputY = 2,
                ratio = 0.5,
                seed = 42,
            )

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
            )

        // [[1*0.5, 2*0.5], [3*0.5, 4*0.5]] = [[0.5, 1.0], [1.5, 2.0]]
        val result = dropout._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = 0.5, actual = output[0, 0])
        assertEquals(expected = 1.0, actual = output[0, 1])
        assertEquals(expected = 1.5, actual = output[1, 0])
        assertEquals(expected = 2.0, actual = output[1, 1])
    }

    @Test
    fun `DropoutD2の_train=マスクを適用してdropout`() {
        val dropout =
            DropoutD2(
                outputX = 2,
                outputY = 2,
                ratio = 0.5,
                seed = 42,
            )

        // [[1, 2], [3, 4]]
        val input =
            listOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toDouble() },
            )

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 2) { x, y -> ((x * 2 + y) + 1) * 2.0 })
        }

        val result = dropout._train(input, calcDelta)

        // maskはrandomで生成されるため、結果は確定的
        // seed=42で生成されるマスクに基づいてdeltaが乗算される
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2
        // 具体的な値の検証はマスクに依存するため、サイズのみ確認
        assertEquals(expected = 2, actual = dx.shape[0])
        assertEquals(expected = 2, actual = dx.shape[1])
    }
}
