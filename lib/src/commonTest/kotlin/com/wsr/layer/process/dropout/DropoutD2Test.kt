@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.dropout

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.get
import com.wsr.core.set
import com.wsr.layer.Context
import com.wsr.layer.process.dropout.DropoutD2
import com.wsr.nextFloat
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
                ratio = 0.5f,
                seed = 42,
            )

        // [[1, 2], [3, 4]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val context = Context(input)

        // Inverted Dropoutのexpect(推論時)では入力をそのまま返す
        val result = dropout._expect(input, context) as Batch<IOType.D2>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 1.0f, actual = output[0, 0])
        assertEquals(expected = 2.0f, actual = output[0, 1])
        assertEquals(expected = 3.0f, actual = output[1, 0])
        assertEquals(expected = 4.0f, actual = output[1, 1])
    }

    @Test
    fun `DropoutD2の_train=マスクを適用してdropout`() {
        val dropout =
            DropoutD2(
                outputX = 2,
                outputY = 2,
                ratio = 0.5f,
                seed = 42,
            )

        // [[1, 2], [3, 4]]
        val input =
            batchOf(
                IOType.d2(2, 2) { x, y -> (x * 2 + y + 1).toFloat() },
            )
        val context = Context(input)

        // deltaは[[2, 4], [6, 8]]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d2(2, 2) { x, y -> ((x * 2 + y) + 1) * 2.0f })
        }

        // seed=42でrandom.nextFloat(0f, 1f)を4回呼び出したときの値を事前計算
        // Inverted Dropoutでは、マスクは0または1/ratio (= 2.0f)
        val testRandom = Random(42)
        val q = 1.0f / 0.5f // 2.0f
        val expectedMask = IOType.d2(2, 2) { _, _ ->
            if (testRandom.nextFloat(0f, 1f) <= 0.5f) q else 0.0f
        }

        val result = dropout._train(input, context, calcDelta) as Batch<IOType.D2>
        // maskに基づいてdeltaが乗算される
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = 2.0f * expectedMask[0, 0], actual = dx[0, 0])
        assertEquals(expected = 4.0f * expectedMask[0, 1], actual = dx[0, 1])
        assertEquals(expected = 6.0f * expectedMask[1, 0], actual = dx[1, 0])
        assertEquals(expected = 8.0f * expectedMask[1, 1], actual = dx[1, 1])
    }
}
