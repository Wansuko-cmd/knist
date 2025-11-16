@file:Suppress("NonAsciiCharacters")

package com.wsr.converter.linear

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearD3Test {
    @Test
    fun `LinearD3のencode=入力をそのまま返す`() {
        val converter = LinearD3(outputX = 2, outputY = 3, outputZ = 4)

        val input = listOf(
            IOType.d3(2, 3, 4) { x, y, z -> (x * 12 + y * 4 + z).toFloat() },
            IOType.d3(2, 3, 4) { x, y, z -> (x * 12 + y * 4 + z + 100).toFloat() },
        )

        val result = converter.encode(input)

        assertEquals(expected = 2, actual = result.size)

        // 入力がそのまま返される
        assertEquals(expected = input[0], actual = result[0])
        assertEquals(expected = input[1], actual = result[1])
    }

    @Test
    fun `LinearD3のdecode=入力をそのまま返す`() {
        val converter = LinearD3(outputX = 2, outputY = 3, outputZ = 4)

        val input = listOf(
            IOType.d3(2, 3, 4) { x, y, z -> (x * 12 + y * 4 + z).toFloat() },
            IOType.d3(2, 3, 4) { x, y, z -> (x * 12 + y * 4 + z + 100).toFloat() },
        )

        val result = converter.decode(input)

        assertEquals(expected = 2, actual = result.size)

        // 入力がそのまま返される
        assertEquals(expected = input[0], actual = result[0])
        assertEquals(expected = input[1], actual = result[1])
    }

    @Test
    fun `LinearD3の往復変換=encode後にdecodeしても同じ結果になる`() {
        val converter = LinearD3(outputX = 2, outputY = 3, outputZ = 4)

        val input = listOf(
            IOType.d3(2, 3, 4) { x, y, z -> (x + y + z).toFloat() },
        )

        // encode -> decode
        val encoded = converter.encode(input)
        val decoded = converter.decode(encoded)

        assertEquals(expected = input.size, actual = decoded.size)
        for (i in input.indices) {
            for (x in 0 until input[i].shape[0]) {
                for (y in 0 until input[i].shape[1]) {
                    for (z in 0 until input[i].shape[2]) {
                        assertEquals(expected = input[i][x, y, z], actual = decoded[i][x, y, z])
                    }
                }
            }
        }
    }

    @Test
    fun `LinearD3の往復変換=decode後にencodeしても同じ結果になる`() {
        val converter = LinearD3(outputX = 2, outputY = 3, outputZ = 4)

        val input = listOf(
            IOType.d3(2, 3, 4) { x, y, z -> (x * y * z).toFloat() },
        )

        // decode -> encode
        val decoded = converter.decode(input)
        val encoded = converter.encode(decoded)

        assertEquals(expected = input.size, actual = encoded.size)
        for (i in input.indices) {
            for (x in 0 until input[i].shape[0]) {
                for (y in 0 until input[i].shape[1]) {
                    for (z in 0 until input[i].shape[2]) {
                        assertEquals(expected = input[i][x, y, z], actual = encoded[i][x, y, z])
                    }
                }
            }
        }
    }

    @Test
    fun `LinearD3のoutputX_outputY_outputZ=設定した値を返す`() {
        val converter = LinearD3(outputX = 3, outputY = 5, outputZ = 7)

        assertEquals(expected = 3, actual = converter.outputX)
        assertEquals(expected = 5, actual = converter.outputY)
        assertEquals(expected = 7, actual = converter.outputZ)
    }
}
