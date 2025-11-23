@file:Suppress("NonAsciiCharacters", "UNCHECKED_CAST")

package com.wsr.layer.process.dropout

import com.wsr.batch.Batch
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.core.set
import com.wsr.layer.Context
import com.wsr.layer.process.dropout.DropoutD1
import com.wsr.nextFloat
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
class DropoutD1Test {
    @Test
    fun `DropoutD1の_expect=入力をそのまま返す`() {
        val dropout =
            DropoutD1(
                outputSize = 3,
                ratio = 0.5f,
                seed = 42,
            )

        // [[1, 2, 3]]
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        val context = Context(input)

        // Inverted Dropoutのexpect(推論時)では入力をそのまま返す
        val result = dropout._expect(input, context) as Batch<IOType.D1>
        assertEquals(expected = 1, actual = result.size)
        val output = result[0]
        assertEquals(expected = 1.0f, actual = output[0])
        assertEquals(expected = 2.0f, actual = output[1])
        assertEquals(expected = 3.0f, actual = output[2])
    }

    @Test
    fun `DropoutD1の_train=マスクを適用してdropout`() {
        val dropout =
            DropoutD1(
                outputSize = 3,
                ratio = 0.5f,
                seed = 42,
            )

        // [[1, 2, 3]]
        val input =
            batchOf(
                IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            )
        val context = Context(input)

        // deltaは[2, 4, 6]を返す
        val calcDelta: (Batch<IOType>) -> Batch<IOType> = {
            batchOf(IOType.d1(listOf(2.0f, 4.0f, 6.0f)))
        }

        // seed=42でrandom.nextFloat(0f, 1f)を3回呼び出したときの値を事前計算
        // Inverted Dropoutでは、マスクは0または1/ratio (= 2.0f)
        val testRandom = Random(42)
        val q = 1.0f / 0.5f // 2.0f
        val expectedMask = List(3) { if (testRandom.nextFloat(0f, 1f) <= 0.5f) q else 0.0f }

        val result = dropout._train(input, context, calcDelta) as Batch<IOType.D1>
        // maskに基づいてdeltaが乗算される
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0]
        assertEquals(expected = 2.0f * expectedMask[0], actual = dx[0])
        assertEquals(expected = 4.0f * expectedMask[1], actual = dx[1])
        assertEquals(expected = 6.0f * expectedMask[2], actual = dx[2])
    }
}
