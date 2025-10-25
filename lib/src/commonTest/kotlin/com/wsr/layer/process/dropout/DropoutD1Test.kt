@file:Suppress("NonAsciiCharacters")

package com.wsr.layer.process.dropout

import com.wsr.IOType
import com.wsr.layer.process.dropout.DropoutD1
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class DropoutD1Test {
    @Test
    fun `DropoutD1の_expect=入力にratioを掛ける`() {
        val dropout =
            DropoutD1(
                outputSize = 3,
                ratio = 0.5,
                seed = 42,
            )

        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
            )

        // [1*0.5, 2*0.5, 3*0.5] = [0.5, 1.0, 1.5]
        val result = dropout._expect(input)

        assertEquals(expected = 1, actual = result.size)
        val output = result[0] as IOType.D1
        assertEquals(expected = 0.5, actual = output[0])
        assertEquals(expected = 1.0, actual = output[1])
        assertEquals(expected = 1.5, actual = output[2])
    }

    @Test
    fun `DropoutD1の_train=マスクを適用してdropout`() {
        val dropout =
            DropoutD1(
                outputSize = 3,
                ratio = 0.5,
                seed = 42,
            )

        // [[1, 2, 3]]
        val input =
            listOf(
                IOType.d1(listOf(1.0, 2.0, 3.0)),
            )

        // deltaは[2, 4, 6]を返す
        val calcDelta: (List<IOType>) -> List<IOType> = {
            listOf(IOType.d1(listOf(2.0, 4.0, 6.0)))
        }

        // seed=42でrandom.nextDouble(0.0, 1.0)を3回呼び出したときの値を事前計算
        val testRandom = Random(42)
        val expectedMask = List(3) { if (testRandom.nextDouble(0.0, 1.0) <= 0.5) 1.0 else 0.0 }

        val result = dropout._train(input, calcDelta)

        // maskに基づいてdeltaが乗算される
        assertEquals(expected = 1, actual = result.size)
        val dx = result[0] as IOType.D1
        assertEquals(expected = 2.0 * expectedMask[0], actual = dx[0])
        assertEquals(expected = 4.0 * expectedMask[1], actual = dx[1])
        assertEquals(expected = 6.0 * expectedMask[2], actual = dx[2])
    }
}
