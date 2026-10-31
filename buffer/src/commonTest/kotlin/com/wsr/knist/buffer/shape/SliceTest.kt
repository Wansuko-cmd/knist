@file:Suppress("NonAsciiCharacters")

package com.wsr.knist.buffer.shape

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.buffer.assertContentEquals
import com.wsr.knist.buffer.bufferTestRule
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SliceTest {
    @Test
    fun `slice=指定範囲を1次元スライス`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(24) { it.toFloat() })

        val actual = Backend.slice(x = x, indices = 10 downTo 4 step 2)

        assertContentEquals(
            expected = DataBuffer.create(10f, 8f, 6f, 4f),
            actual = actual,
        )
    }

    @Test
    fun `slice_axis0=指定範囲を2次元スライス`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(24) { it.toFloat() })

        val actual = Backend.slice(x = x, xi = 6, xj = 4, axis = 0, indices = 3 downTo 1 step 2)

        assertContentEquals(
            expected = DataBuffer.create(
                floatArrayOf(
                    12f,
                    13f,
                    14f,
                    15f,
                    4f,
                    5f,
                    6f,
                    7f,
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun `slice_axis1=指定範囲を2次元スライス`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(24) { it.toFloat() })

        val actual = Backend.slice(x = x, xi = 4, xj = 6, axis = 1, indices = 3 downTo 1 step 2)

        assertContentEquals(
            expected = DataBuffer.create(
                floatArrayOf(
                    3f,
                    1f,
                    9f,
                    7f,
                    15f,
                    13f,
                    21f,
                    19f,
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun `slice_axis0=指定範囲3次元スライス`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(36) { it.toFloat() })

        val actual = Backend.slice(x = x, xi = 6, xj = 3, xk = 2, axis = 0, indices = 3 downTo 1 step 2)

        assertContentEquals(
            expected = DataBuffer.create(
                floatArrayOf(
                    18f, 19f, 20f, 21f, 22f, 23f,
                    6f, 7f, 8f, 9f, 10f, 11f,
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun `slice_axis1=指定範囲3次元スライス`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(36) { it.toFloat() })

        val actual = Backend.slice(x = x, xi = 3, xj = 6, xk = 2, axis = 1, indices = 3 downTo 1 step 2)

        assertContentEquals(
            expected = DataBuffer.create(
                floatArrayOf(
                    6f, 7f,
                    2f, 3f,

                    18f, 19f,
                    14f, 15f,

                    30f, 31f,
                    26f, 27f,
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun `slice_axis2=指定範囲3次元スライス`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(36) { it.toFloat() })

        val actual = Backend.slice(x = x, xi = 3, xj = 2, xk = 6, axis = 2, indices = 3 downTo 1 step 2)

        assertContentEquals(
            expected = DataBuffer.create(
                floatArrayOf(
                    3f, 1f,
                    9f, 7f,

                    15f, 13f,
                    21f, 19f,

                    27f, 25f,
                    33f, 31f,
                ),
            ),
            actual = actual,
        )
    }

    @Test
    fun `slice=空の範囲は例外`() = bufferTestRule {
        val x = DataBuffer.create(FloatArray(24) { it.toFloat() })

        assertFailsWith<IllegalArgumentException> { Backend.slice(x = x, indices = 0 until 0) }
        assertFailsWith<IllegalArgumentException> { Backend.slice(x = x, xi = 4, xj = 6, axis = 1, indices = 4 until 4 step 2) }
        assertFailsWith<IllegalArgumentException> { Backend.slice(x = x, xi = 2, xj = 3, xk = 4, axis = 0, indices = 1 until 1) }
    }
}
