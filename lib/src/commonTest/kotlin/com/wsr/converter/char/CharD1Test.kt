@file:Suppress("NonAsciiCharacters")

package com.wsr.converter.char

import com.wsr.IOType
import kotlin.test.Test
import kotlin.test.assertEquals

class CharD1Test {
    @Test
    fun `CharD1のencode=文字をone-hotベクトルに変換する`() {
        val converter = CharD1()

        val input = listOf('a', 'b', 'c')

        val result = converter.encode(input)

        assertEquals(expected = 3, actual = result.size)

        // 'a' -> index 1
        val output0 = result[0]
        assertEquals(expected = CharD1.vocabSize, actual = output0.shape[0])
        assertEquals(expected = 0.0, actual = output0[0])
        assertEquals(expected = 1.0, actual = output0[1])
        assertEquals(expected = 0.0, actual = output0[2])

        // 'b' -> index 2
        val output1 = result[1]
        assertEquals(expected = 0.0, actual = output1[0])
        assertEquals(expected = 0.0, actual = output1[1])
        assertEquals(expected = 1.0, actual = output1[2])
        assertEquals(expected = 0.0, actual = output1[3])

        // 'c' -> index 3
        val output2 = result[2]
        assertEquals(expected = 0.0, actual = output2[0])
        assertEquals(expected = 0.0, actual = output2[1])
        assertEquals(expected = 0.0, actual = output2[2])
        assertEquals(expected = 1.0, actual = output2[3])
    }

    @Test
    fun `CharD1のencode=未知文字はindex0に変換される`() {
        val converter = CharD1()

        val input = listOf('A', '1', '@')

        val result = converter.encode(input)

        assertEquals(expected = 3, actual = result.size)

        // 未知文字 -> index 0
        result.forEach { output ->
            assertEquals(expected = 1.0, actual = output[0])
            assertEquals(expected = 0.0, actual = output[1])
        }
    }

    @Test
    fun `CharD1のdecode=one-hotベクトルを文字に変換する`() {
        val converter = CharD1()

        val input = listOf(
            IOType.d1(CharD1.vocabSize).also { it[1] = 1.0 }, // 'a'
            IOType.d1(CharD1.vocabSize).also { it[2] = 1.0 }, // 'b'
            IOType.d1(CharD1.vocabSize).also { it[3] = 1.0 }, // 'c'
        )

        val result = converter.decode(input)

        assertEquals(expected = 3, actual = result.size)
        assertEquals(expected = 'a', actual = result[0])
        assertEquals(expected = 'b', actual = result[1])
        assertEquals(expected = 'c', actual = result[2])
    }

    @Test
    fun `CharD1のdecode=最大値のインデックスを文字に変換する`() {
        val converter = CharD1()

        // Softmax出力のような確率分布を想定
        val input = listOf(
            // index 1 ('a') が最大
            IOType.d1(CharD1.vocabSize).also {
                it[0] = 0.1
                it[1] = 0.6
                it[2] = 0.2
                it[3] = 0.1
            },
            // index 27 ('.') が最大
            IOType.d1(CharD1.vocabSize).also {
                it[26] = 0.1
                it[27] = 0.8
                it[28] = 0.05
                it[29] = 0.05
            },
        )

        val result = converter.decode(input)

        assertEquals(expected = 2, actual = result.size)
        assertEquals(expected = 'a', actual = result[0])
        assertEquals(expected = '.', actual = result[1])
    }

    @Test
    fun `CharD1の往復変換=encode後にdecodeすると元に戻る`() {
        val converter = CharD1()

        val input = listOf('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd', '!')

        // encode -> decode
        val encoded = converter.encode(input)
        val decoded = converter.decode(encoded)

        assertEquals(expected = input, actual = decoded)
    }

    @Test
    fun `CharD1の往復変換=decode後にencodeしてもone-hotベクトルになる`() {
        val converter = CharD1()

        // 確率分布からdecode -> encode
        val input = listOf(
            IOType.d1(CharD1.vocabSize).also {
                it[0] = 0.1
                it[1] = 0.6 // 'a' が最大
                it[2] = 0.2
                it[3] = 0.1
            },
        )

        val decoded = converter.decode(input)
        val encoded = converter.encode(decoded)

        assertEquals(expected = 1, actual = encoded.size)

        // One-hotベクトルになる
        val output = encoded[0]
        assertEquals(expected = 0.0, actual = output[0])
        assertEquals(expected = 1.0, actual = output[1]) // 'a'
        assertEquals(expected = 0.0, actual = output[2])
        assertEquals(expected = 0.0, actual = output[3])
    }
}
