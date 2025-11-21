@file:Suppress("NonAsciiCharacters")

package com.wsr.converter.word

import com.wsr.IOType
import com.wsr.batchOf
import kotlin.test.Test
import kotlin.test.assertEquals
import com.wsr.get
class WordD2Test {
    private val words = listOf("<PAD>", "<UNK>", "hello", "world", "test")
    private val length = 3
    private val unknownIndex = 1

    @Test
    fun `WordD2のencode=単語列をone-hotベクトルの列に変換する`() {
        val converter = WordD2(
            words = words,
            length = length,
            unknownIndex = unknownIndex,
        )

        val input = listOf(
            listOf("hello", "world", "test"),
            listOf("test", "hello", "world"),
        )

        val result = converter.encode(input)

        assertEquals(expected = 2, actual = result.size)

        // バッチ1
        val output0 = result[0] as IOType.D2
        assertEquals(expected = 3, actual = output0.shape[0])
        assertEquals(expected = 5, actual = output0.shape[1])
        assertEquals(expected = 1.0f, actual = output0[0, 2]) // hello
        assertEquals(expected = 1.0f, actual = output0[1, 3]) // world
        assertEquals(expected = 1.0f, actual = output0[2, 4]) // test

        // バッチ2
        val output1 = result[1] as IOType.D2
        assertEquals(expected = 3, actual = output1.shape[0])
        assertEquals(expected = 5, actual = output1.shape[1])
        assertEquals(expected = 1.0f, actual = output1[0, 4]) // test
        assertEquals(expected = 1.0f, actual = output1[1, 2]) // hello
        assertEquals(expected = 1.0f, actual = output1[2, 3]) // world
    }

    @Test
    fun `WordD2のdecode=one-hotベクトルの列を単語列に変換する`() {
        val converter = WordD2(
            words = words,
            length = length,
            unknownIndex = unknownIndex,
        )

        val input = batchOf(
            IOType.d2(3, 5) { x, y ->
                when {
                    x == 0 && y == 2 -> 1.0f // hello
                    x == 1 && y == 3 -> 1.0f // world
                    x == 2 && y == 4 -> 1.0f // test
                    else -> 0.0f
                }
            },
        )

        val result = converter.decode(input)

        assertEquals(expected = 1, actual = result.size)
        assertEquals(expected = 3, actual = result[0].size)
        assertEquals(expected = "hello", actual = result[0][0])
        assertEquals(expected = "world", actual = result[0][1])
        assertEquals(expected = "test", actual = result[0][2])
    }

    @Test
    fun `WordD2のdecode=最大値のインデックスを単語に変換する`() {
        val converter = WordD2(
            words = words,
            length = length,
            unknownIndex = unknownIndex,
        )

        val input = batchOf(
            // バッチ1: hello, world, test
            IOType.d2(3, 5) { x, y ->
                when {
                    x == 0 && y == 2 -> 1.0f
                    x == 1 && y == 3 -> 1.0f
                    x == 2 && y == 4 -> 1.0f
                    else -> 0.0f
                }
            },
            // バッチ2: test, hello, world
            IOType.d2(3, 5) { x, y ->
                when {
                    x == 0 && y == 4 -> 1.0f
                    x == 1 && y == 2 -> 1.0f
                    x == 2 && y == 3 -> 1.0f
                    else -> 0.0f
                }
            },
        )

        val result = converter.decode(input)

        assertEquals(expected = 2, actual = result.size)

        // バッチ1
        assertEquals(expected = 3, actual = result[0].size)
        assertEquals(expected = "hello", actual = result[0][0])
        assertEquals(expected = "world", actual = result[0][1])
        assertEquals(expected = "test", actual = result[0][2])

        // バッチ2
        assertEquals(expected = 3, actual = result[1].size)
        assertEquals(expected = "test", actual = result[1][0])
        assertEquals(expected = "hello", actual = result[1][1])
        assertEquals(expected = "world", actual = result[1][2])
    }

    @Test
    fun `WordD2の往復変換=encode後にdecodeすると元に戻る`() {
        val converter = WordD2(
            words = words,
            length = length,
            unknownIndex = unknownIndex,
        )

        val input = listOf(
            listOf("hello", "world", "test"),
            listOf("test", "hello", "world"),
        )

        // encode -> decode
        val encoded = converter.encode(input)
        val decoded = converter.decode(encoded)

        assertEquals(expected = input, actual = decoded)
    }

    @Test
    fun `WordD2の往復変換=decode後にencodeしてもone-hotベクトルになる`() {
        val converter = WordD2(
            words = words,
            length = length,
            unknownIndex = unknownIndex,
        )

        // 確率分布からdecode -> encode
        val input = batchOf(
            IOType.d2(3, 5) { x, y ->
                when (x) {
                    0 -> listOf(0.1f, 0.2f, 0.5f, 0.15f, 0.05f)[y] // hello
                    1 -> listOf(0.05f, 0.1f, 0.15f, 0.6f, 0.1f)[y] // world
                    2 -> listOf(0.05f, 0.05f, 0.1f, 0.2f, 0.6f)[y] // test
                    else -> 0.0f
                }
            },
        )

        val decoded = converter.decode(input)
        val encoded = converter.encode(decoded)

        assertEquals(expected = 1, actual = encoded.size)

        // One-hotベクトルになる
        val output = encoded[0] as IOType.D2
        assertEquals(expected = 3, actual = output.shape[0])
        assertEquals(expected = 5, actual = output.shape[1])

        // hello (index 2)
        assertEquals(expected = 1.0f, actual = output[0, 2])
        assertEquals(expected = 0.0f, actual = output[0, 0])
        assertEquals(expected = 0.0f, actual = output[0, 1])
        assertEquals(expected = 0.0f, actual = output[0, 3])
        assertEquals(expected = 0.0f, actual = output[0, 4])

        // world (index 3)
        assertEquals(expected = 1.0f, actual = output[1, 3])

        // test (index 4)
        assertEquals(expected = 1.0f, actual = output[2, 4])
    }
}
