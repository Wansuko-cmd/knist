@file:Suppress("NonAsciiCharacters")

package com.wsr.converter.word

import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.get
import kotlin.test.Test
import kotlin.test.assertEquals
class WordD1Test {
    private val words = listOf("<PAD>", "<UNK>", "hello", "world", "test")
    private val unknownIndex = 1

    @Test
    fun `WordD1のencode=単語をone-hotベクトルに変換する`() {
        val converter = WordD1(
            words = words,
            unknownIndex = unknownIndex,
        )

        val input = listOf("hello", "world", "test")

        val result = converter.encode(input)

        assertEquals(expected = 3, actual = result.size)

        // "hello" -> [0.0f, 0.0f, 1.0f, 0.0f, 0.0f] (index 2)
        val output0 = result[0]
        assertEquals(expected = 5, actual = output0.shape[0])
        assertEquals(expected = 0.0f, actual = output0[0])
        assertEquals(expected = 0.0f, actual = output0[1])
        assertEquals(expected = 1.0f, actual = output0[2])
        assertEquals(expected = 0.0f, actual = output0[3])
        assertEquals(expected = 0.0f, actual = output0[4])

        // "world" -> [0.0f, 0.0f, 0.0f, 1.0f, 0.0f] (index 3)
        val output1 = result[1]
        assertEquals(expected = 0.0f, actual = output1[0])
        assertEquals(expected = 0.0f, actual = output1[1])
        assertEquals(expected = 0.0f, actual = output1[2])
        assertEquals(expected = 1.0f, actual = output1[3])
        assertEquals(expected = 0.0f, actual = output1[4])

        // "test" -> [0.0f, 0.0f, 0.0f, 0.0f, 1.0f] (index 4)
        val output2 = result[2]
        assertEquals(expected = 0.0f, actual = output2[0])
        assertEquals(expected = 0.0f, actual = output2[1])
        assertEquals(expected = 0.0f, actual = output2[2])
        assertEquals(expected = 0.0f, actual = output2[3])
        assertEquals(expected = 1.0f, actual = output2[4])
    }

    @Test
    fun `WordD1のencode=未知語はunknownIndexに変換される`() {
        val converter = WordD1(
            words = words,
            unknownIndex = unknownIndex,
        )

        val input = listOf("unknown", "hello")

        val result = converter.encode(input)

        assertEquals(expected = 2, actual = result.size)

        // "unknown" -> [0.0f, 1.0f, 0.0f, 0.0f, 0.0f] (unknownIndex=1)
        val output0 = result[0]
        assertEquals(expected = 0.0f, actual = output0[0])
        assertEquals(expected = 1.0f, actual = output0[1])
        assertEquals(expected = 0.0f, actual = output0[2])
        assertEquals(expected = 0.0f, actual = output0[3])
        assertEquals(expected = 0.0f, actual = output0[4])

        // "hello" -> [0.0f, 0.0f, 1.0f, 0.0f, 0.0f] (index 2)
        val output1 = result[1]
        assertEquals(expected = 0.0f, actual = output1[0])
        assertEquals(expected = 0.0f, actual = output1[1])
        assertEquals(expected = 1.0f, actual = output1[2])
        assertEquals(expected = 0.0f, actual = output1[3])
        assertEquals(expected = 0.0f, actual = output1[4])
    }

    @Test
    fun `WordD1のdecode=one-hotベクトルを単語に変換する`() {
        val converter = WordD1(
            words = words,
            unknownIndex = unknownIndex,
        )

        val input = batchOf(
            IOType.d1(listOf(0.0f, 0.0f, 1.0f, 0.0f, 0.0f)), // hello
            IOType.d1(listOf(0.0f, 0.0f, 0.0f, 1.0f, 0.0f)), // world
            IOType.d1(listOf(0.0f, 0.0f, 0.0f, 0.0f, 1.0f)), // test
        )

        val result = converter.decode(input)

        assertEquals(expected = 3, actual = result.size)
        assertEquals(expected = "hello", actual = result[0])
        assertEquals(expected = "world", actual = result[1])
        assertEquals(expected = "test", actual = result[2])
    }

    @Test
    fun `WordD1のdecode=最大値のインデックスを単語に変換する`() {
        val converter = WordD1(
            words = words,
            unknownIndex = unknownIndex,
        )

        // Softmax出力のような確率分布を想定
        val input = batchOf(
            IOType.d1(listOf(0.1f, 0.2f, 0.5f, 0.15f, 0.05f)), // index 2 (hello) が最大
            IOType.d1(listOf(0.05f, 0.1f, 0.15f, 0.6f, 0.1f)), // index 3 (world) が最大
        )

        val result = converter.decode(input)

        assertEquals(expected = 2, actual = result.size)
        assertEquals(expected = "hello", actual = result[0])
        assertEquals(expected = "world", actual = result[1])
    }

    @Test
    fun `WordD1の往復変換=encode後にdecodeすると元に戻る`() {
        val converter = WordD1(
            words = words,
            unknownIndex = unknownIndex,
        )

        val input = listOf("hello", "world", "test")

        // encode -> decode
        val encoded = converter.encode(input)
        val decoded = converter.decode(encoded)

        assertEquals(expected = input, actual = decoded)
    }

    @Test
    fun `WordD1の往復変換=decode後にencodeしてもone-hotベクトルになる`() {
        val converter = WordD1(
            words = words,
            unknownIndex = unknownIndex,
        )

        // 確率分布からdecode -> encode
        val input = batchOf(
            IOType.d1(listOf(0.1f, 0.2f, 0.5f, 0.15f, 0.05f)), // hello
        )

        val decoded = converter.decode(input)
        val encoded = converter.encode(decoded)

        assertEquals(expected = 1, actual = encoded.size)

        // One-hotベクトルになる
        val output = encoded[0]
        assertEquals(expected = 0.0f, actual = output[0])
        assertEquals(expected = 0.0f, actual = output[1])
        assertEquals(expected = 1.0f, actual = output[2]) // hello
        assertEquals(expected = 0.0f, actual = output[3])
        assertEquals(expected = 0.0f, actual = output[4])
    }
}
