package com.wsr.converter.word

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class WordsD1(
    override val outputSize: Int,
    private val words: List<String>,
    private val unknownIndex: Int,
    private val paddingIndex: Int,
) : Converter.D1<List<String>>() {
    val vocabSize = words.size
    private val wordToId = words.mapIndexed { index, word -> word to index.toFloat() }.toMap()

    override fun encode(input: List<List<String>>): List<IOType.D1> = input.map { sentence ->
        val tokenIds = sentence
            .take(outputSize)
            .map { wordToId[it] ?: unknownIndex.toFloat() }

        IOType.d1(outputSize) { paddingIndex.toFloat() }.also {
            tokenIds.toFloatArray().copyInto(it.value)
        }
    }

    override fun decode(input: List<IOType.D1>): List<List<String>> = input.map { input ->
        input.value
            .toList()
            .mapNotNull { id ->
                val index = id.toInt()
                if (index == paddingIndex) null else words.getOrNull(index)
            }
    }
}

fun NetworkBuilder.Companion.wordsD1(
    maxLength: Int,
    words: List<String>,
    unknownIndex: Int,
    paddingIndex: Int,
    optimizer: Optimizer,
    initializer: WeightInitializer,
): NetworkBuilder.D1<List<String>> {
    check(unknownIndex in words.indices) { "unknownIndex must be within words range." }
    check(paddingIndex in words.indices) { "paddingIndex must be within words range." }

    return inputD1(
        converter = WordsD1(
            outputSize = maxLength,
            words = words,
            unknownIndex = unknownIndex,
            paddingIndex = paddingIndex,
        ),
        optimizer = optimizer,
        initializer = initializer,
    )
}
