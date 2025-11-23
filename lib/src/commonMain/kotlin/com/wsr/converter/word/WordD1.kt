package com.wsr.converter.word

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.core.d1
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import com.wsr.batch.toBatch
import com.wsr.batch.toList
import com.wsr.core.collection.index.maxIndex
import com.wsr.core.set
import kotlinx.serialization.Serializable

@Serializable
class WordD1(private val words: List<String>, private val unknownIndex: Int) : Converter.D1<String>() {
    override val outputSize = words.size
    private val wordToId = words.mapIndexed { index, word -> word to index }.toMap()

    override fun encode(input: List<String>): Batch<IOType.D1> = input.toList().map { text ->
        val id = wordToId[text] ?: unknownIndex
        IOType.d1(outputSize).also { it[id] = 1f }
    }.toBatch()

    override fun decode(input: Batch<IOType.D1>): List<String> = input.toList().map { input -> words[input.maxIndex()] }
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
