@file:Suppress("NonAsciiCharacters")

package com.wsr.reshape.transpose

import com.wsr.core.d4
import com.wsr.core.IOType
import com.wsr.core.get
import com.wsr.core.reshape.transpose.transpose
import kotlin.test.Test
import kotlin.test.assertEquals

class D4ExtTest {
    @Test
    fun `transpose_恒等変換`() {
        val input = IOType.d4(2, 3, 4, 5) { i, j, k, l -> (i * 60 + j * 20 + k * 5 + l).toFloat() }
        val result = input.transpose(0, 1, 2, 3)

        assertEquals(expected = listOf(2, 3, 4, 5), actual = result.shape)
        assertEquals(expected = input[0, 0, 0, 0], actual = result[0, 0, 0, 0])
        assertEquals(expected = input[1, 2, 3, 4], actual = result[1, 2, 3, 4])
    }

    @Test
    fun `transpose_最初の2軸を入れ替え`() {
        val input = IOType.d4(2, 3, 4, 5) { i, j, k, l -> (i * 60 + j * 20 + k * 5 + l).toFloat() }
        val result = input.transpose(1, 0, 2, 3)

        assertEquals(expected = listOf(3, 2, 4, 5), actual = result.shape)
        // result[j, i, k, l] = input[i, j, k, l]
        assertEquals(expected = input[0, 0, 0, 0], actual = result[0, 0, 0, 0])
        assertEquals(expected = input[0, 1, 0, 0], actual = result[1, 0, 0, 0])
        assertEquals(expected = input[1, 0, 0, 0], actual = result[0, 1, 0, 0])
        assertEquals(expected = input[1, 2, 3, 4], actual = result[2, 1, 3, 4])
    }

    @Test
    fun `transpose_最後の2軸を入れ替え`() {
        val input = IOType.d4(2, 3, 4, 5) { i, j, k, l -> (i * 60 + j * 20 + k * 5 + l).toFloat() }
        val result = input.transpose(0, 1, 3, 2)

        assertEquals(expected = listOf(2, 3, 5, 4), actual = result.shape)
        // result[i, j, l, k] = input[i, j, k, l]
        assertEquals(expected = input[0, 0, 0, 0], actual = result[0, 0, 0, 0])
        assertEquals(expected = input[0, 0, 0, 1], actual = result[0, 0, 1, 0])
        assertEquals(expected = input[1, 2, 3, 4], actual = result[1, 2, 4, 3])
    }

    @Test
    fun `transpose_逆順`() {
        val input = IOType.d4(2, 3, 4, 5) { i, j, k, l -> (i * 60 + j * 20 + k * 5 + l).toFloat() }
        val result = input.transpose(3, 2, 1, 0)

        assertEquals(expected = listOf(5, 4, 3, 2), actual = result.shape)
        // result[l, k, j, i] = input[i, j, k, l]
        assertEquals(expected = input[0, 0, 0, 0], actual = result[0, 0, 0, 0])
        assertEquals(expected = input[1, 2, 3, 4], actual = result[4, 3, 2, 1])
    }

    @Test
    fun `transpose_BatchFirst変換`() {
        // [channel, batch, height, width] -> [batch, channel, height, width]
        val input = IOType.d4(3, 2, 4, 5) { c, b, h, w -> (c * 40 + b * 20 + h * 5 + w).toFloat() }
        val result = input.transpose(1, 0, 2, 3)

        assertEquals(expected = listOf(2, 3, 4, 5), actual = result.shape)
        // result[b, c, h, w] = input[c, b, h, w]
        assertEquals(expected = input[0, 0, 0, 0], actual = result[0, 0, 0, 0])
        assertEquals(expected = input[2, 1, 3, 4], actual = result[1, 2, 3, 4])
    }
}
