@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.position

import com.wsr.IOType
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals

class RoPED2Test {
    @Test
    fun `RoPED2の_expect=入力に回転を適用`() {
        val rope = RoPED2(outputX = 2, outputY = 4, waveLength = 10000.0)

        // 入力は[[1, 2, 3, 4], [5, 6, 7, 8]]
        val input =
            listOf(
                IOType.d2(2, 4) { x, y -> (x * 4 + y + 1).toDouble() },
            )

        val result = rope._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2

        // RoPEの計算を手動で検証
        // theta[i] = 1 / 10000^(2i/d)
        val theta0 = 1.0 / 10000.0.pow(0.0 / 4.0) // 1.0
        val theta1 = 1.0 / 10000.0.pow(2.0 / 4.0) // 0.01

        // pos=0の場合
        val cos00 = cos(0 * theta0) // cos(0) = 1.0
        val sin00 = sin(0 * theta0) // sin(0) = 0.0
        val cos01 = cos(0 * theta1) // cos(0) = 1.0
        val sin01 = sin(0 * theta1) // sin(0) = 0.0

        // 偶数次元: x * cos - y * sin
        // 奇数次元: x * sin + y * cos
        val out00 = 1.0 * cos00 - 2.0 * sin00 // 1.0 * 1.0 - 2.0 * 0.0 = 1.0
        val out01 = 1.0 * sin00 + 2.0 * cos00 // 1.0 * 0.0 + 2.0 * 1.0 = 2.0
        val out02 = 3.0 * cos01 - 4.0 * sin01 // 3.0 * 1.0 - 4.0 * 0.0 = 3.0
        val out03 = 3.0 * sin01 + 4.0 * cos01 // 3.0 * 0.0 + 4.0 * 1.0 = 4.0

        assertEquals(expected = out00, actual = output[0, 0], absoluteTolerance = 1e-6)
        assertEquals(expected = out01, actual = output[0, 1], absoluteTolerance = 1e-6)
        assertEquals(expected = out02, actual = output[0, 2], absoluteTolerance = 1e-6)
        assertEquals(expected = out03, actual = output[0, 3], absoluteTolerance = 1e-6)

        // pos=1の場合
        val cos10 = cos(1 * theta0) // cos(1)
        val sin10 = sin(1 * theta0) // sin(1)
        val cos11 = cos(1 * theta1) // cos(0.01)
        val sin11 = sin(1 * theta1) // sin(0.01)

        val out10 = 5.0 * cos10 - 6.0 * sin10
        val out11 = 5.0 * sin10 + 6.0 * cos10
        val out12 = 7.0 * cos11 - 8.0 * sin11
        val out13 = 7.0 * sin11 + 8.0 * cos11

        assertEquals(expected = out10, actual = output[1, 0], absoluteTolerance = 1e-6)
        assertEquals(expected = out11, actual = output[1, 1], absoluteTolerance = 1e-6)
        assertEquals(expected = out12, actual = output[1, 2], absoluteTolerance = 1e-6)
        assertEquals(expected = out13, actual = output[1, 3], absoluteTolerance = 1e-6)
    }

    @Test
    fun `RoPED2の_train=順伝播と逆伝播で回転を適用`() {
        val rope = RoPED2(outputX = 2, outputY = 4, waveLength = 10000.0)

        // 入力は[[1, 2, 3, 4], [5, 6, 7, 8]]
        val input =
            listOf(
                IOType.d2(2, 4) { x, y -> (x * 4 + y + 1).toDouble() },
            )

        // deltaは[[0.1, 0.2, 0.3, 0.4], [0.5, 0.6, 0.7, 0.8]]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d2(2, 4) { x, y -> (x * 4 + y + 1) * 0.1 })
        }

        val result = rope._train(input, calcDelta)

        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2

        // 逆伝播でもRoPEを適用するので、deltaに回転が適用される
        val theta0 = 1.0 / 10000.0.pow(0.0 / 4.0)
        val theta1 = 1.0 / 10000.0.pow(2.0 / 4.0)

        // pos=0, delta=[0.1, 0.2, 0.3, 0.4]
        val cos00 = cos(0 * theta0)
        val sin00 = sin(0 * theta0)
        val cos01 = cos(0 * theta1)
        val sin01 = sin(0 * theta1)

        val dx00 = 0.1 * cos00 - 0.2 * sin00
        val dx01 = 0.1 * sin00 + 0.2 * cos00
        val dx02 = 0.3 * cos01 - 0.4 * sin01
        val dx03 = 0.3 * sin01 + 0.4 * cos01

        assertEquals(expected = dx00, actual = dx[0, 0], absoluteTolerance = 1e-6)
        assertEquals(expected = dx01, actual = dx[0, 1], absoluteTolerance = 1e-6)
        assertEquals(expected = dx02, actual = dx[0, 2], absoluteTolerance = 1e-6)
        assertEquals(expected = dx03, actual = dx[0, 3], absoluteTolerance = 1e-6)
    }
}
