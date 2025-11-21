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

class RoPED2Test {
    @Test
    fun `RoPED2の_expect=入力に回転を適用`() {
        val rope = RoPED2(outputX = 2, outputY = 4, waveLength = 10000.0f)

        // 入力は[[1, 2, 3, 4], [5, 6, 7, 8]]
        val input =
            batchOf(
                IOType.d2(2, 4) { x, y -> (x * 4 + y + 1).toFloat() },
            )
        val context = Context(input)

        val result = rope._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]

        // RoPEの計算を手動で検証
        // theta[i] = 1 / 10000^(2i/d)
        val theta0 = 1.0f / 10000.0f.pow(0.0f / 4.0f) // 1.0f
        val theta1 = 1.0f / 10000.0f.pow(2.0f / 4.0f) // 0.01f

        // pos=0の場合
        val cos00 = cos(0 * theta0) // cos(0) = 1.0f
        val sin00 = sin(0 * theta0) // sin(0) = 0.0f
        val cos01 = cos(0 * theta1) // cos(0) = 1.0f
        val sin01 = sin(0 * theta1) // sin(0) = 0.0f

        // 偶数次元: x * cos - y * sin
        // 奇数次元: x * sin + y * cos
        val out00 = 1.0f * cos00 - 2.0f * sin00 // 1.0f * 1.0f - 2.0f * 0.0f = 1.0f
        val out01 = 1.0f * sin00 + 2.0f * cos00 // 1.0f * 0.0f + 2.0f * 1.0f = 2.0f
        val out02 = 3.0f * cos01 - 4.0f * sin01 // 3.0f * 1.0f - 4.0f * 0.0f = 3.0f
        val out03 = 3.0f * sin01 + 4.0f * cos01 // 3.0f * 0.0f + 4.0f * 1.0f = 4.0f

        assertEquals(expected = out00, actual = output[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = out01, actual = output[0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = out02, actual = output[0, 2], absoluteTolerance = 1e-6f)
        assertEquals(expected = out03, actual = output[0, 3], absoluteTolerance = 1e-6f)

        // pos=1の場合
        val cos10 = cos(1 * theta0) // cos(1)
        val sin10 = sin(1 * theta0) // sin(1)
        val cos11 = cos(1 * theta1) // cos(0.01f)
        val sin11 = sin(1 * theta1) // sin(0.01f)

        val out10 = 5.0f * cos10 - 6.0f * sin10
        val out11 = 5.0f * sin10 + 6.0f * cos10
        val out12 = 7.0f * cos11 - 8.0f * sin11
        val out13 = 7.0f * sin11 + 8.0f * cos11

        assertEquals(expected = out10, actual = output[1, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = out11, actual = output[1, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = out12, actual = output[1, 2], absoluteTolerance = 1e-6f)
        assertEquals(expected = out13, actual = output[1, 3], absoluteTolerance = 1e-6f)
    }

    @Test
    fun `RoPED2の_train=順伝播と逆伝播で回転を適用`() {
        val rope = RoPED2(outputX = 2, outputY = 4, waveLength = 10000.0f)

        // 入力は[[1, 2, 3, 4], [5, 6, 7, 8]]
        val input =
            batchOf(
                IOType.d2(2, 4) { x, y -> (x * 4 + y + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[0.1f, 0.2f, 0.3f, 0.4f], [0.5f, 0.6f, 0.7f, 0.8f]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 4) { x, y -> (x * 4 + y + 1) * 0.1f })
        }

        val result = rope._train(input, context, calcDelta) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]

        // 逆伝播でもRoPEを適用するので、deltaに回転が適用される
        val theta0 = 1.0f / 10000.0f.pow(0.0f / 4.0f)
        val theta1 = 1.0f / 10000.0f.pow(2.0f / 4.0f)

        // pos=0, delta=[0.1f, 0.2f, 0.3f, 0.4f]
        val cos00 = cos(0 * theta0)
        val sin00 = sin(0 * theta0)
        val cos01 = cos(0 * theta1)
        val sin01 = sin(0 * theta1)

        val dx00 = 0.1f * cos00 - 0.2f * sin00
        val dx01 = 0.1f * sin00 + 0.2f * cos00
        val dx02 = 0.3f * cos01 - 0.4f * sin01
        val dx03 = 0.3f * sin01 + 0.4f * cos01

        assertEquals(expected = dx00, actual = dx[0, 0], absoluteTolerance = 1e-6f)
        assertEquals(expected = dx01, actual = dx[0, 1], absoluteTolerance = 1e-6f)
        assertEquals(expected = dx02, actual = dx[0, 2], absoluteTolerance = 1e-6f)
        assertEquals(expected = dx03, actual = dx[0, 3], absoluteTolerance = 1e-6f)
    }
}
