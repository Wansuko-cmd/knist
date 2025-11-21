package com.wsr.converter.word

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.maxIndex
import com.wsr.converter.Converter
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class WordD2(private val words: List<String>, private val length: Int, private val unknownIndex: Int) :
    Converter.D2<List<String>>() {
    override val outputX = length
    override val outputY = words.size
    private val wordToId = words.mapIndexed { index, word -> word to index }.toMap()

    override fun encode(input: List<List<String>>): List<IOType.D2> = input.map { text ->
        val result = IOType.d2(outputX, outputY)
        text.forEachIndexed { index, word ->
            val id = wordToId[word] ?: unknownIndex
            result[index] = IOType.d1(outputY).also { it[id] = 1f }
        }
        for (index in text.size until outputX) {
            result[index] = IOType.d1(outputY).also { it[0] = 1f }
        }
        result
    }

    override fun decode(input: List<IOType.D2>): List<List<String>> = input.map { input ->
        (0 until length).map { words[input[it].maxIndex()] }
    }
}

fun NetworkBuilder.Companion.wordD2(
    words: List<String>,
    length: Int,
    unknownIndex: Int,
    optimizer: Optimizer,
    initializer: WeightInitializer,
): NetworkBuilder.D2<List<String>> {
    check(unknownIndex in words.indices) { "unknownIndex must be within words range." }

    return inputD2(
        converter = WordD2(
            words = words,
            length = length,
            unknownIndex = unknownIndex,
        ),
        optimizer = optimizer,
        initializer = initializer,
    )
}
