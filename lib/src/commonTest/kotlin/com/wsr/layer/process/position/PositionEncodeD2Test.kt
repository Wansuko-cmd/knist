@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.position

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.get
import com.wsr.layer.Context
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals

class PositionEncodeD2Test {
    @Test
    fun `PositionEncodeD2の_expect=入力に位置エンコーディングを加算`() {
        val positionEncode = PositionEncodeD2(outputX = 2, outputY = 4, waveLength = 10000.0f)

        // 入力は全て1.0（位置エンコーディングの影響だけを見るため）
        val input =
            batchOf(
                IOType.d2(2, 4) { _, _ -> 1.0f },
            )
        val context = Context(input)

        val result = positionEncode._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2

        // 位置エンコーディングの計算
        // PE(pos, 2i) = sin(pos / 10000^(2i/d_model))
        // PE(pos, 2i+1) = cos(pos / 10000^(2i/d_model))
        // 偶数次元と次の奇数次元で同じ周波数を使用

        // pos=0の場合（x=0）
        val pe00 = sin(0.0f / 10000.0f.pow(0.0f / 4.0f)) // sin(0) = 0.0f
        val pe01 = cos(0.0f / 10000.0f.pow(0.0f / 4.0f)) // cos(0) = 1.0f (y=1 -> (y-1)=0)
        val pe02 = sin(0.0f / 10000.0f.pow(2.0f / 4.0f)) // sin(0) = 0.0f
        val pe03 = cos(0.0f / 10000.0f.pow(2.0f / 4.0f)) // cos(0) = 1.0f (y=3 -> (y-1)=2)

        assertEquals(expected = 1.0f + pe00, actual = output[0, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 1.0f + pe01, actual = output[0, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 1.0f + pe02, actual = output[0, 2], absoluteTolerance = 1e-4f)
        assertEquals(expected = 1.0f + pe03, actual = output[0, 3], absoluteTolerance = 1e-4f)

        // pos=1の場合（x=1）
        val pe10 = sin(1.0f / 10000.0f.pow(0.0f / 4.0f)) // sin(1)
        val pe11 = cos(1.0f / 10000.0f.pow(0.0f / 4.0f)) // cos(1) (y=1 -> (y-1)=0)
        val pe12 = sin(1.0f / 10000.0f.pow(2.0f / 4.0f)) // sin(1 / 100)
        val pe13 = cos(1.0f / 10000.0f.pow(2.0f / 4.0f)) // cos(1 / 100) (y=3 -> (y-1)=2)

        assertEquals(expected = 1.0f + pe10, actual = output[1, 0], absoluteTolerance = 1e-4f)
        assertEquals(expected = 1.0f + pe11, actual = output[1, 1], absoluteTolerance = 1e-4f)
        assertEquals(expected = 1.0f + pe12, actual = output[1, 2], absoluteTolerance = 1e-4f)
        assertEquals(expected = 1.0f + pe13, actual = output[1, 3], absoluteTolerance = 1e-4f)
    }

    @Test
    fun `PositionEncodeD2の_train=deltaをそのまま返す`() {
        val positionEncode = PositionEncodeD2(outputX = 2, outputY = 3, waveLength = 10000.0f)

        // 入力は[[1, 2, 3], [4, 5, 6]]
        val input =
            batchOf(
                IOType.d2(2, 3) { x, y -> (x * 3 + y + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[0.1f, 0.2f, 0.3f], [0.4f, 0.5f, 0.6f]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 3) { x, y -> (x * 3 + y + 1) * 0.1f })
        }

        val result = positionEncode._train(input, context, calcDelta) as Batch<IOType.D2>
        // 位置エンコーディングの逆伝播は、加算なのでdeltaをそのまま返す
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = 0.1f, actual = dx[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.2f, actual = dx[0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.3f, actual = dx[0, 2], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.4f, actual = dx[1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.5f, actual = dx[1, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = 0.6f, actual = dx[1, 2], absoluteTolerance = 1e-6f)
    }
}
