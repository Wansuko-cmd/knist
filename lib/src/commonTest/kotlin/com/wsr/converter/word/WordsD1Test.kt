@file:Suppress("NonAsciiCharacters")

package com.wsr.converter.word

import com.wsr.core.IOType
import com.wsr.batch.batchOf
import com.wsr.batch.get
import com.wsr.core.d1
import com.wsr.core.get
import com.wsr.core.set
import kotlin.test.Test
import kotlin.test.assertEquals
class WordsD1Test {
    private val words = listOf("<PAD>", "<UNK>", "hello", "world", "test")
    private val paddingIndex = 0
    private val unknownIndex = 1

    @Test
    fun `WordsD1のencode=単語リストをIDのリストに変換する`() {
        val converter = WordsD1(
            outputSize = 5,
            words = words,
            unknownIndex = unknownIndex,
            paddingIndex = paddingIndex,
        )

        val input = listOf(
            listOf("hello", "world"),
            listOf("test"),
        )

        val result = converter.encode(input)

        assertEquals(expected = 2, actual = result.size)

        // 1つ目: ["hello", "world"] -> [2.0f, 3.0f, 0.0f, 0.0f, 0.0f]
        val output0 = result[0]
        assertEquals(expected = 2.0f, actual = output0[0])
        assertEquals(expected = 3.0f, actual = output0[1])
        assertEquals(expected = 0.0f, actual = output0[2]) // padding
        assertEquals(expected = 0.0f, actual = output0[3]) // padding
        assertEquals(expected = 0.0f, actual = output0[4]) // padding

        // 2つ目: ["test"] -> [4.0f, 0.0f, 0.0f, 0.0f, 0.0f]
        val output1 = result[1]
        assertEquals(expected = 4.0f, actual = output1[0])
        assertEquals(expected = 0.0f, actual = output1[1]) // padding
        assertEquals(expected = 0.0f, actual = output1[2]) // padding
        assertEquals(expected = 0.0f, actual = output1[3]) // padding
        assertEquals(expected = 0.0f, actual = output1[4]) // padding
    }

    @Test
    fun `WordsD1のencode=未知語はunknownIndexに変換される`() {
        val converter = WordsD1(
            outputSize = 5,
            words = words,
            unknownIndex = unknownIndex,
            paddingIndex = paddingIndex,
        )

        val input = listOf(
            listOf("hello", "unknown", "world"),
        )

        val result = converter.encode(input)

        assertEquals(expected = 1, actual = result.size)

        // ["hello", "unknown", "world"] -> [2.0f, 1.0f, 3.0f, 0.0f, 0.0f]
        val output = result[0]
        assertEquals(expected = 2.0f, actual = output[0]) // hello
        assertEquals(expected = 1.0f, actual = output[1]) // unknown -> <UNK>
        assertEquals(expected = 3.0f, actual = output[2]) // world
        assertEquals(expected = 0.0f, actual = output[3]) // padding
        assertEquals(expected = 0.0f, actual = output[4]) // padding
    }

    @Test
    fun `WordsD1のencode=outputSizeを超える入力は切り捨てられる`() {
        val converter = WordsD1(
            outputSize = 3,
            words = words,
            unknownIndex = unknownIndex,
            paddingIndex = paddingIndex,
        )

        val input = listOf(
            listOf("hello", "world", "test", "hello", "world"),
        )

        val result = converter.encode(input)

        assertEquals(expected = 1, actual = result.size)

        // ["hello", "world", "test", "hello", "world"] -> [2.0f, 3.0f, 4.0f]
        val output = result[0]
        assertEquals(expected = 3, actual = output.shape[0])
        assertEquals(expected = 2.0f, actual = output[0])
        assertEquals(expected = 3.0f, actual = output[1])
        assertEquals(expected = 4.0f, actual = output[2])
    }

    @Test
    fun `WordsD1のdecode=IDのリストを単語リストに変換する`() {
        val converter = WordsD1(
            outputSize = 5,
            words = words,
            unknownIndex = unknownIndex,
            paddingIndex = paddingIndex,
        )

        val input = batchOf(
            IOType.d1(listOf(2.0f, 3.0f, 0.0f, 0.0f, 0.0f)),
            IOType.d1(listOf(4.0f, 0.0f, 0.0f, 0.0f, 0.0f)),
        )

        val result = converter.decode(input)

        assertEquals(expected = 2, actual = result.size)

        // [2.0f, 3.0f, 0.0f, 0.0f, 0.0f] -> ["hello", "world"] (paddingは削除)
        assertEquals(expected = listOf("hello", "world"), actual = result[0])

        // [4.0f, 0.0f, 0.0f, 0.0f, 0.0f] -> ["test"] (paddingは削除)
        assertEquals(expected = listOf("test"), actual = result[1])
    }

    @Test
    fun `WordsD1のdecode=未知のインデックスは無視される`() {
        val converter = WordsD1(
            outputSize = 5,
            words = words,
            unknownIndex = unknownIndex,
            paddingIndex = paddingIndex,
        )

        val input = batchOf(
            IOType.d1(listOf(2.0f, 99.0f, 3.0f, 0.0f, 0.0f)),
        )

        val result = converter.decode(input)

        assertEquals(expected = 1, actual = result.size)

        // [2.0f, 99.0f, 3.0f, 0.0f, 0.0f] -> ["hello", "world"] (99は範囲外なので無視)
        assertEquals(expected = listOf("hello", "world"), actual = result[0])
    }

    @Test
    fun `WordsD1のdecode=unknownIndexはUNKに変換される`() {
        val converter = WordsD1(
            outputSize = 5,
            words = words,
            unknownIndex = unknownIndex,
            paddingIndex = paddingIndex,
        )

        val input = batchOf(
            IOType.d1(listOf(2.0f, 1.0f, 3.0f, 0.0f, 0.0f)),
        )

        val result = converter.decode(input)

        assertEquals(expected = 1, actual = result.size)

        // [2.0f, 1.0f, 3.0f, 0.0f, 0.0f] -> ["hello", "<UNK>", "world"]
        assertEquals(expected = listOf("hello", "<UNK>", "world"), actual = result[0])
    }

    @Test
    fun `WordsD1の往復変換=encode後にdecodeすると元に戻る（パディングは削除される）`() {
        val converter = WordsD1(
            outputSize = 5,
            words = words,
            unknownIndex = unknownIndex,
            paddingIndex = paddingIndex,
        )

        val input = listOf(
            listOf("hello", "world"),
            listOf("test"),
        )

        // encode -> decode
        val encoded = converter.encode(input)
        val decoded = converter.decode(encoded)

        assertEquals(expected = input, actual = decoded)
    }

    @Test
    fun `WordsD1の往復変換=decode後にencodeしても同じ結果になる`() {
        val converter = WordsD1(
            outputSize = 5,
            words = words,
            unknownIndex = unknownIndex,
            paddingIndex = paddingIndex,
        )

        val input = batchOf(
            IOType.d1(listOf(2.0f, 3.0f, 0.0f, 0.0f, 0.0f)),
        )

        // decode -> encode
        val decoded = converter.decode(input)
        val encoded = converter.encode(decoded)

        // 結果は元の入力と同じになる
        assertEquals(expected = input.size, actual = encoded.size)
        for (i in 0 until input.size) {
            for (j in 0 until input[i].shape[0]) {
                assertEquals(expected = input[i][j], actual = encoded[i][j])
            }
        }
    }
}
