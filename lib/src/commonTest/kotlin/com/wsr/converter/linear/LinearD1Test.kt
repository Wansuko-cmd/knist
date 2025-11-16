@file:Suppress("NonAsciiCharacters")

package com.wsr.converter.linear

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearD1Test {
    @Test
    fun `LinearD1のencode=入力をそのまま返す`() {
        val converter = LinearD1(outputSize = 3)

        val input = listOf(
            IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            IOType.d1(listOf(4.0f, 5.0f, 6.0f)),
        )

        val result = converter.encode(input)

        assertEquals(expected = 2, actual = result.size)

        // 入力がそのまま返される
        assertEquals(expected = input[0], actual = result[0])
        assertEquals(expected = input[1], actual = result[1])
    }

    @Test
    fun `LinearD1のdecode=入力をそのまま返す`() {
        val converter = LinearD1(outputSize = 3)

        val input = listOf(
            IOType.d1(listOf(1.0f, 2.0f, 3.0f)),
            IOType.d1(listOf(4.0f, 5.0f, 6.0f)),
        )

        val result = converter.decode(input)

        assertEquals(expected = 2, actual = result.size)

        // 入力がそのまま返される
        assertEquals(expected = input[0], actual = result[0])
        assertEquals(expected = input[1], actual = result[1])
    }

    @Test
    fun `LinearD1の往復変換=encode後にdecodeしても同じ結果になる`() {
        val converter = LinearD1(outputSize = 5)

        val input = listOf(
            IOType.d1(listOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)),
            IOType.d1(listOf(6.0f, 7.0f, 8.0f, 9.0f, 10.0f)),
        )

        // encode -> decode
        val encoded = converter.encode(input)
        val decoded = converter.decode(encoded)

        assertEquals(expected = input.size, actual = decoded.size)
        for (i in input.indices) {
            for (j in 0 until input[i].shape[0]) {
                assertEquals(expected = input[i][j], actual = decoded[i][j])
            }
        }
    }

    @Test
    fun `LinearD1の往復変換=decode後にencodeしても同じ結果になる`() {
        val converter = LinearD1(outputSize = 5)

        val input = listOf(
            IOType.d1(listOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)),
        )

        // decode -> encode
        val decoded = converter.decode(input)
        val encoded = converter.encode(decoded)

        assertEquals(expected = input.size, actual = encoded.size)
        for (i in input.indices) {
            for (j in 0 until input[i].shape[0]) {
                assertEquals(expected = input[i][j], actual = encoded[i][j])
            }
        }
    }

    @Test
    fun `LinearD1のoutputSize=設定した値を返す`() {
        val converter = LinearD1(outputSize = 10)

        assertEquals(expected = 10, actual = converter.outputSize)
    }
}
