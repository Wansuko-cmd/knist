@file:Suppress("NonAsciiCharacters")

package com.wsr.converter.char

import com.wsr.IOType
import com.wsr.batchOf
import com.wsr.collection.forEach
import com.wsr.get
import kotlin.test.Test
import kotlin.test.assertEquals

class CharsD1Test {
    @Test
    fun `CharsD1のencode=文字列を文字IDのリストに変換する`() {
        val converter = CharsD1(outputSize = 10)

        val input = listOf("hello", "abc")

        val result = converter.encode(input)

        assertEquals(expected = 2, actual = result.size)

        // "hello" -> [8.0f, 5.0f, 12.0f, 12.0f, 15.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f]
        val output0 = result[0]
        assertEquals(expected = 8.0f, actual = output0[0]) // h
        assertEquals(expected = 5.0f, actual = output0[1]) // e
        assertEquals(expected = 12.0f, actual = output0[2]) // l
        assertEquals(expected = 12.0f, actual = output0[3]) // l
        assertEquals(expected = 15.0f, actual = output0[4]) // o
        assertEquals(expected = 0.0f, actual = output0[5]) // padding (space)
        assertEquals(expected = 0.0f, actual = output0[6]) // padding (space)
        assertEquals(expected = 0.0f, actual = output0[7]) // padding (space)
        assertEquals(expected = 0.0f, actual = output0[8]) // padding (space)
        assertEquals(expected = 0.0f, actual = output0[9]) // padding (space)

        // "abc" -> [1.0f, 2.0f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f]
        val output1 = result[1]
        assertEquals(expected = 1.0f, actual = output1[0]) // a
        assertEquals(expected = 2.0f, actual = output1[1]) // b
        assertEquals(expected = 3.0f, actual = output1[2]) // c
        assertEquals(expected = 0.0f, actual = output1[3]) // padding (space)
    }

    @Test
    fun `CharsD1のencode=outputSizeを超える文字は切り捨てられる`() {
        val converter = CharsD1(outputSize = 3)

        val input = listOf("hello")

        val result = converter.encode(input)

        assertEquals(expected = 1, actual = result.size)

        // "hello" -> [8.0f, 5.0f, 12.0f] (最初の3文字のみ)
        val output = result[0]
        assertEquals(expected = 3, actual = output.shape[0])
        assertEquals(expected = 8.0f, actual = output[0]) // h
        assertEquals(expected = 5.0f, actual = output[1]) // e
        assertEquals(expected = 12.0f, actual = output[2]) // l
    }

    @Test
    fun `CharsD1のencode=語彙にない文字は0に変換される`() {
        val converter = CharsD1(outputSize = 5)

        // '漢' は語彙に含まれていない
        val input = listOf("a漢b")

        val result = converter.encode(input)

        assertEquals(expected = 1, actual = result.size)

        // "a漢b" -> [1.0f, 0.0f, 2.0f, 0.0f, 0.0f]
        val output = result[0]
        assertEquals(expected = 1.0f, actual = output[0]) // a
        assertEquals(expected = 0.0f, actual = output[1]) // 漢 (unknown)
        assertEquals(expected = 2.0f, actual = output[2]) // b
        assertEquals(expected = 0.0f, actual = output[3]) // padding (space)
        assertEquals(expected = 0.0f, actual = output[4]) // padding (space)
    }

    @Test
    fun `CharsD1のencode=特殊文字も正しく変換される`() {
        val converter = CharsD1(outputSize = 10)

        val input = listOf("hello, world!")

        val result = converter.encode(input)

        assertEquals(expected = 1, actual = result.size)

        val output = result[0]
        assertEquals(expected = 8.0f, actual = output[0]) // h
        assertEquals(expected = 5.0f, actual = output[1]) // e
        assertEquals(expected = 12.0f, actual = output[2]) // l
        assertEquals(expected = 12.0f, actual = output[3]) // l
        assertEquals(expected = 15.0f, actual = output[4]) // o
        assertEquals(expected = 28.0f, actual = output[5]) // ,
        assertEquals(expected = 0.0f, actual = output[6]) // (space)
        assertEquals(expected = 23.0f, actual = output[7]) // w
        assertEquals(expected = 15.0f, actual = output[8]) // o
        assertEquals(expected = 18.0f, actual = output[9]) // r
    }

    @Test
    fun `CharsD1のdecode=文字IDのリストを文字列に変換する`() {
        val converter = CharsD1(outputSize = 10)

        val input = batchOf(
            IOType.d1(listOf(8.0f, 5.0f, 12.0f, 12.0f, 15.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)),
            IOType.d1(listOf(1.0f, 2.0f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)),
        )

        val result = converter.decode(input)

        assertEquals(expected = 2, actual = result.size)

        // [8.0f, 5.0f, 12.0f, 12.0f, 15.0f, 0.0f, ...] -> "hello     " (0.0はspace)
        assertEquals(expected = "hello     ", actual = result[0])

        // [1.0f, 2.0f, 3.0f, 0.0f, ...] -> "abc       " (0.0はspace)
        assertEquals(expected = "abc       ", actual = result[1])
    }

    @Test
    fun `CharsD1のdecode=範囲外のIDは無視される`() {
        val converter = CharsD1(outputSize = 5)

        val input = batchOf(
            IOType.d1(listOf(1.0f, 99.0f, 2.0f, 0.0f, 0.0f)),
        )

        val result = converter.decode(input)

        assertEquals(expected = 1, actual = result.size)

        // [1.0f, 99.0f, 2.0f, 0.0f, 0.0f] -> "ab  " (99は範囲外なので無視、0.0はspace)
        assertEquals(expected = "ab  ", actual = result[0])
    }

    @Test
    fun `CharsD1の往復変換=encode後にdecodeすると元に戻る（paddingあり）`() {
        val converter = CharsD1(outputSize = 10)

        val input = listOf("hello", "test")

        // encode -> decode
        val encoded = converter.encode(input)
        val decoded = converter.decode(encoded)

        assertEquals(expected = 2, actual = decoded.size)

        // paddingが含まれるため、長さはoutputSizeと同じになる
        assertEquals(expected = "hello     ", actual = decoded[0])
        assertEquals(expected = "test      ", actual = decoded[1])
    }

    @Test
    fun `CharsD1の往復変換=decode後にencodeしても同じ結果になる`() {
        val converter = CharsD1(outputSize = 10)

        val input = batchOf(
            IOType.d1(listOf(8.0f, 5.0f, 12.0f, 12.0f, 15.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)),
        )

        // decode -> encode
        val decoded = converter.decode(input)
        val encoded = converter.encode(decoded)

        // 結果は元の入力と同じになる
        assertEquals(expected = input.size, actual = encoded.size)
        assertEquals(expected = input, actual = encoded)
    }

    @Test
    fun `CharsD1のvocabSize=語彙数を返す`() {
        // " abcdefghijklmnopqrstuvwxyz.,!?" の文字数
        assertEquals(expected = 31, actual = CharsD1.vocabSize)
    }

    @Test
    fun `CharsD1のencode=空文字列は全てpaddingになる`() {
        val converter = CharsD1(outputSize = 5)

        val input = listOf("")

        val result = converter.encode(input)

        assertEquals(expected = 1, actual = result.size)

        // "" -> [0.0f, 0.0f, 0.0f, 0.0f, 0.0f]
        val output = result[0]
        for (i in 0 until 5) {
            assertEquals(expected = 0.0f, actual = output[i])
        }
    }
}
