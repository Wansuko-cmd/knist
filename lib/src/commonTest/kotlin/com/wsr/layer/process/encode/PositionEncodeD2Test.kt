@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.encode

import com.wsr.IOType
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals

class PositionEncodeD2Test {
    @Test
    fun `PositionEncodeD2の_expect=入力に位置エンコーディングを加算`() {
        val positionEncode = PositionEncodeD2(outputX = 2, outputY = 4)

        // 入力は全て1.0（位置エンコーディングの影響だけを見るため）
        val input =
            listOf(
                IOType.d2(2, 4) { _, _ -> 1.0 },
            )

        val result = positionEncode._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2

        // 位置エンコーディングの計算
        // PE(pos, dim) = sin or cos(pos / 10000^(dim/d_model))
        // 偶数次元: sin, 奇数次元: cos

        // pos=0の場合（x=0）
        val pe_0_0 = sin(0.0 / 10000.0.pow(0.0 / 4.0)) // sin(0) = 0.0
        val pe_0_1 = cos(0.0 / 10000.0.pow(1.0 / 4.0)) // cos(0) = 1.0
        val pe_0_2 = sin(0.0 / 10000.0.pow(2.0 / 4.0)) // sin(0) = 0.0
        val pe_0_3 = cos(0.0 / 10000.0.pow(3.0 / 4.0)) // cos(0) = 1.0

        assertEquals(expected = 1.0 + pe_0_0, actual = output[0, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 1.0 + pe_0_1, actual = output[0, 1], absoluteTolerance = 1e-4)
        assertEquals(expected = 1.0 + pe_0_2, actual = output[0, 2], absoluteTolerance = 1e-4)
        assertEquals(expected = 1.0 + pe_0_3, actual = output[0, 3], absoluteTolerance = 1e-4)

        // pos=1の場合（x=1）
        val pe_1_0 = sin(1.0 / 10000.0.pow(0.0 / 4.0)) // sin(1)
        val pe_1_1 = cos(1.0 / 10000.0.pow(1.0 / 4.0)) // cos(1 / 10000^0.25)
        val pe_1_2 = sin(1.0 / 10000.0.pow(2.0 / 4.0)) // sin(1 / 100)
        val pe_1_3 = cos(1.0 / 10000.0.pow(3.0 / 4.0)) // cos(1 / 10000^0.75)

        assertEquals(expected = 1.0 + pe_1_0, actual = output[1, 0], absoluteTolerance = 1e-4)
        assertEquals(expected = 1.0 + pe_1_1, actual = output[1, 1], absoluteTolerance = 1e-4)
        assertEquals(expected = 1.0 + pe_1_2, actual = output[1, 2], absoluteTolerance = 1e-4)
        assertEquals(expected = 1.0 + pe_1_3, actual = output[1, 3], absoluteTolerance = 1e-4)
    }

    @Test
    fun `PositionEncodeD2の_train=deltaをそのまま返す`() {
        val positionEncode = PositionEncodeD2(outputX = 2, outputY = 3)

        // 入力は[[1, 2, 3], [4, 5, 6]]
        val input =
            listOf(
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toDouble() },
            )

        // deltaは[[0.1, 0.2, 0.3], [0.4, 0.5, 0.6]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 3) { x, y -> (x * 3 + y + 1) * 0.1 })
        }

        val result = positionEncode._train(input, calcDelta)

        // 位置エンコーディングの逆伝播は、加算なのでdeltaをそのまま返す
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2
        assertEquals(expected = 0.1, actual = dx[0, 0], absoluteTolerance = 1e-6)
        assertEquals(expected = 0.2, actual = dx[0, 1], absoluteTolerance = 1e-6)
        assertEquals(expected = 0.3, actual = dx[0, 2], absoluteTolerance = 1e-6)
        assertEquals(expected = 0.4, actual = dx[1, 0], absoluteTolerance = 1e-6)
        assertEquals(expected = 0.5, actual = dx[1, 1], absoluteTolerance = 1e-6)
        assertEquals(expected = 0.6, actual = dx[1, 2], absoluteTolerance = 1e-6)
    }
}
