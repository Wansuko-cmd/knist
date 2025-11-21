@file:Suppress("NonAsciiCharacters")

package com.wsr.converter.linear

import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.get
import kotlin.test.Test
import kotlin.test.assertEquals
class LinearD2Test {
    @Test
    fun `LinearD2のencode=入力をそのまま返す`() {
        val converter = LinearD2(outputX = 2, outputY = 3)

        val input = listOf(
            IOType.d2(2, 3) { x, y -> (x * 3 + y).toFloat() },
            IOType.d2(2, 3) { x, y -> (x * 3 + y + 10).toFloat() },
        )

        val result = converter.encode(input)

        assertEquals(expected = 2, actual = result.size)

        // 入力がそのまま返される
        assertEquals(expected = input[0], actual = result[0])
        assertEquals(expected = input[1], actual = result[1])
    }

    @Test
    fun `LinearD2のdecode=入力をそのまま返す`() {
        val converter = LinearD2(outputX = 2, outputY = 3)

        val input = batchOf(
            IOType.d2(2, 3) { x, y -> (x * 3 + y).toFloat() },
            IOType.d2(2, 3) { x, y -> (x * 3 + y + 10).toFloat() },
        )

        val result = converter.decode(input)

        assertEquals(expected = 2, actual = result.size)

        // 入力がそのまま返される
        assertEquals(expected = input[0], actual = result[0])
        assertEquals(expected = input[1], actual = result[1])
    }

    @Test
    fun `LinearD2の往復変換=encode後にdecodeしても同じ結果になる`() {
        val converter = LinearD2(outputX = 3, outputY = 4)

        val input = listOf(
            IOType.d2(3, 4) { x, y -> (x + y).toFloat() },
        )

        // encode -> decode
        val encoded = converter.encode(input)
        val decoded = converter.decode(encoded)

        assertEquals(expected = input.size, actual = decoded.size)
        for (i in input.indices) {
            for (x in 0 until input[i].shape[0]) {
                for (y in 0 until input[i].shape[1]) {
                    assertEquals(expected = input[i][x, y], actual = decoded[i][x, y])
                }
            }
        }
    }

    @Test
    fun `LinearD2の往復変換=decode後にencodeしても同じ結果になる`() {
        val converter = LinearD2(outputX = 3, outputY = 4)

        val input = batchOf(
            IOType.d2(3, 4) { x, y -> (x * y).toFloat() },
        )

        // decode -> encode
        val decoded = converter.decode(input)
        val encoded = converter.encode(decoded)

        assertEquals(expected = input.size, actual = encoded.size)
        assertEquals(expected = input, actual = encoded)
    }

    @Test
    fun `LinearD2のoutputXとoutputY=設定した値を返す`() {
        val converter = LinearD2(outputX = 5, outputY = 7)

        assertEquals(expected = 5, actual = converter.outputX)
        assertEquals(expected = 7, actual = converter.outputY)
    }
}
