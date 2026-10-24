@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.buffer.shape

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.buffer.assertContentEquals
import com.wsr.knist.buffer.bufferTestRule
import kotlin.test.Test

class CopyIntoTest {
    @Test
    fun `copyInto=指定範囲に1次元コピー`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(4) { it.toFloat() })
        val actual = DataBuffer.create(15)

        Backend.copyInto(x = x, y = actual, indices = 10 downTo 4 step 2)

        assertContentEquals(
            expected = DataBuffer.create(
                floatArrayOf(
                    0f, 0f, 0f, 0f, 3f,
                    0f, 2f, 0f, 1f, 0f,
                    0f, 0f, 0f, 0f, 0f,
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun `copyInto_axis0=指定範囲に2次元コピー`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(8) { it.toFloat() })
        val actual = DataBuffer.create(24)

        Backend.copyInto(x = x, y = actual, yi = 6, yj = 4, axis = 0, indices = 3 downTo 1 step 2)

        assertContentEquals(
            expected = DataBuffer.create(
                floatArrayOf(
                    0f, 0f, 0f, 0f,
                    4f, 5f, 6f, 7f,
                    0f, 0f, 0f, 0f,
                    0f, 1f, 2f, 3f,
                    0f, 0f, 0f, 0f,
                    0f, 0f, 0f, 0f,
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun `copyInto_axis1=指定範囲に2次元コピー`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(12) { it.toFloat() })
        val actual = DataBuffer.create(24)

        Backend.copyInto(x = x, y = actual, yi = 6, yj = 4, axis = 1, indices = 3 downTo 1 step 2)

        assertContentEquals(
            expected = DataBuffer.create(
                floatArrayOf(
                    0f, 1f, 0f, 0f,
                    0f, 3f, 0f, 2f,
                    0f, 5f, 0f, 4f,
                    0f, 7f, 0f, 6f,
                    0f, 9f, 0f, 8f,
                    0f, 11f, 0f, 10f,
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun `copyInto_axis0=指定範囲3次元コピー`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(12) { it.toFloat() })
        val actual = DataBuffer.create(36)

        Backend.copyInto(x = x, y = actual, yi = 6, yj = 3, yk = 2, axis = 0, indices = 3 downTo 1 step 2)

        assertContentEquals(
            expected = DataBuffer.create(
                floatArrayOf(
                    0f, 0f, 0f,
                    0f, 0f, 0f,

                    6f, 7f, 8f,
                    9f, 10f, 11f,

                    0f, 0f, 0f,
                    0f, 0f, 0f,

                    0f, 1f, 2f,
                    3f, 4f, 5f,

                    0f, 0f, 0f,
                    0f, 0f, 0f,

                    0f, 0f, 0f,
                    0f, 0f, 0f,
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun `copyInto_axis1=指定範囲3次元コピー`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(12) { it.toFloat() })
        val actual = DataBuffer.create(36)

        Backend.copyInto(x = x, y = actual, yi = 3, yj = 6, yk = 2, axis = 1, indices = 3 downTo 1 step 2)

        assertContentEquals(
            expected = DataBuffer.create(
                floatArrayOf(
                    0f, 0f,
                    2f, 3f,
                    0f, 0f,
                    0f, 1f,
                    0f, 0f,
                    0f, 0f,

                    0f, 0f,
                    6f, 7f,
                    0f, 0f,
                    4f, 5f,
                    0f, 0f,
                    0f, 0f,

                    0f, 0f,
                    10f, 11f,
                    0f, 0f,
                    8f, 9f,
                    0f, 0f,
                    0f, 0f,
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun `copyInto_axis2=指定範囲3次元コピー`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(12) { it.toFloat() })
        val actual = DataBuffer.create(36)

        Backend.copyInto(x = x, y = actual, yi = 3, yj = 2, yk = 6, axis = 2, indices = 3 downTo 1 step 2)

        assertContentEquals(
            expected = DataBuffer.create(
                floatArrayOf(
                    0f, 1f, 0f, 0f, 0f, 0f,
                    0f, 3f, 0f, 2f, 0f, 0f,

                    0f, 5f, 0f, 4f, 0f, 0f,
                    0f, 7f, 0f, 6f, 0f, 0f,

                    0f, 9f, 0f, 8f, 0f, 0f,
                    0f, 11f, 0f, 10f, 0f, 0f,
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun `copyInto=空の範囲なら何も書き込まない`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(4) { it.toFloat() })
        val actual = DataBuffer.create(24)

        Backend.copyInto(x = x, y = actual, indices = 0 until 0)
        Backend.copyInto(x = x, y = actual, yi = 4, yj = 6, axis = 1, indices = 4 until 4 step 2)
        Backend.copyInto(x = x, y = actual, yi = 2, yj = 3, yk = 4, axis = 0, indices = 1 until 1)

        assertContentEquals(expected = DataBuffer.create(24), actual = actual)
    }
}
