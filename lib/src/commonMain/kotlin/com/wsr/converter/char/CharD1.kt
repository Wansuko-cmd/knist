package com.wsr.converter.char

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.maxIndex
import com.wsr.converter.Converter
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import com.wsr.toBatch
import com.wsr.toList
import kotlinx.serialization.Serializable

@Serializable
class CharD1 : Converter.D1<Char>() {
    override val outputSize = chars.size
    override fun encode(input: List<Char>): Batch<IOType.D1> = input.toList().map { char ->
        val id = charToId[char] ?: 0
        IOType.d1(outputSize).also { it[id] = 1f }
    }.toBatch()

    override fun decode(input: Batch<IOType.D1>): List<Char> = input.toList().map { input -> chars[input.maxIndex()] }

    companion object Companion {
        private val chars = " abcdefghijklmnopqrstuvwxyz.,!?".toList()
        private val charToId = chars.mapIndexed { index, char -> char to index }.toMap()
        val vocabSize = chars.size
    }
}

fun NetworkBuilder.Companion.charD1(optimizer: Optimizer, initializer: WeightInitializer) = inputD1(
    converter = CharD1(),
    optimizer = optimizer,
    initializer = initializer,
)
