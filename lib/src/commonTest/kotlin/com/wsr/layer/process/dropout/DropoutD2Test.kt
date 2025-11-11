@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.dropout

import com.wsr.IOType
import com.wsr.layer.process.dropout.DropoutD2
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class DropoutD2Test {
    @Test
    fun `DropoutD2の_expect=入力をそのまま返す`() {
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

        // Inverted Dropoutのexpect(推論時)では入力をそのまま返す
        val result = dropout._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D2
        assertEquals(expected = 1.0, actual = output[0, 0])
        assertEquals(expected = 2.0, actual = output[0, 1])
        assertEquals(expected = 3.0, actual = output[1, 0])
        assertEquals(expected = 4.0, actual = output[1, 1])
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

        // seed=42でrandom.nextDouble(0.0, 1.0)を4回呼び出したときの値を事前計算
        // Inverted Dropoutでは、マスクは0または1/ratio (= 2.0)
        val testRandom = Random(42)
        val q = 1.0 / 0.5  // 2.0
        val expectedMask = IOType.d2(2, 2) { _, _ ->
            if (testRandom.nextDouble(0.0, 1.0) <= 0.5) q else 0.0
        }

        val result = dropout._train(input, calcDelta)

        // maskに基づいてdeltaが乗算される
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D2
        assertEquals(expected = 2.0 * expectedMask[0, 0], actual = dx[0, 0])
        assertEquals(expected = 4.0 * expectedMask[0, 1], actual = dx[0, 1])
        assertEquals(expected = 6.0 * expectedMask[1, 0], actual = dx[1, 0])
        assertEquals(expected = 8.0 * expectedMask[1, 1], actual = dx[1, 1])
    }
}
