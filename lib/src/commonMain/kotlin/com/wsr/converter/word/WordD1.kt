package com.wsr.converter.word

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class WordD1(private val words: List<String>, private val unknownIndex: Int) : Converter.D1<String>() {
    override val outputSize = words.size
    private val wordToId = words.mapIndexed { index, word -> word to index }.toMap()

    override fun encode(input: List<String>): List<IOType.D1> = input.map { text ->
        val id = wordToId[text] ?: unknownIndex
        IOType.d1(outputSize).also { it[id] = 1.0 }
    }

    override fun decode(input: List<IOType.D1>): List<String> = input.map { input ->
        val index = input.maxIndex() ?: unknownIndex
        words[index]
    }

    private fun IOType.D1.maxIndex(): Int? {
        if (value.isEmpty()) return null
        var index = 0
        var max = Double.MIN_VALUE
        for (i in value.indices) {
            if (max < this[i]) {
                index = i
                max = this[i]
            }
        }
        return index
    }
}

fun NetworkBuilder.Companion.wordD1(
    words: List<String>,
    unknownIndex: Int,
    optimizer: Optimizer,
    initializer: WeightInitializer,
): NetworkBuilder.D1<String> {
    check(unknownIndex in words.indices) { "unknownIndex must be within words range." }

    return inputD1(
        converter = WordD1(
            words = words,
            unknownIndex = unknownIndex,
        ),
        optimizer = optimizer,
        initializer = initializer,
    )
}
