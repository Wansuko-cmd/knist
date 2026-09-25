@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.buffer.index

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.buffer.assertContentEquals
import com.wsr.knist.buffer.bufferTestRule
import kotlin.test.Test

class ScatterAddTest {
    @Test
    fun `scatterAdd=Yの値を元にXを圧縮する`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(48) { it.toFloat() })
        val y = DataBuffer.create(FloatArray(6) { it.toFloat() % 3 })

        val actual = Backend.scatterAdd(
            x = x,
            y = y,
            i = 2,
            j = 3,
            k = 4,
            b = 1,
        )

        assertContentEquals(
            expected = DataBuffer.create(
                floatArrayOf(
                    12f, 14f, 16f, 18f,
                    20f, 22f, 24f, 26f,
                    28f, 30f, 32f, 34f,

                    60f, 62f, 64f, 66f,
                    68f, 70f, 72f, 74f,
                    76f, 78f, 80f, 82f,
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun `scatterAdd=バッチごとに異なるYの値を元にXを圧縮する`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(16) { it.toFloat() })
        val y = DataBuffer.create(floatArrayOf(0f, 1f, 2f, 0f))

        val actual = Backend.scatterAdd(
            x = x,
            y = y,
            i = 2,
            j = 3,
            k = 2,
            b = 2,
        )

        assertContentEquals(
            expected = DataBuffer.create(
                floatArrayOf(
                    10f, 12f,
                    2f, 3f,
                    8f, 9f,

                    18f, 20f,
                    6f, 7f,
                    12f, 13f,
                ),
            ),
            actual = actual,
        )
    }
}
