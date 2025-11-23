package com.wsr.converter.char

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.toBatch
import com.wsr.batch.toList
import com.wsr.converter.Converter
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class CharsD1(override val outputSize: Int) : Converter.D1<String>() {
    override fun encode(input: List<String>): Batch<IOType.D1> = input.toList().map { text ->
        IOType.d1(outputSize) { index ->
            text.getOrNull(index)
                ?.let { charToId[it] }
                ?: 0f
        }
    }.toBatch()

    override fun decode(input: Batch<IOType.D1>): List<String> = input.toList().map { input ->
        input.value
            .map { chars.getOrNull(it.toInt()) }
            .filterNotNull()
            .joinToString("")
    }

    companion object Companion {
        private val chars = " abcdefghijklmnopqrstuvwxyz.,!?".toList()
        private val charToId = chars.mapIndexed { index, char -> char to index.toFloat() }.toMap()
        val vocabSize = chars.size
    }
}

fun NetworkBuilder.Companion.charsD1(maxLength: Int, optimizer: Optimizer, initializer: WeightInitializer) = inputD1(
    converter = CharsD1(maxLength),
    optimizer = optimizer,
    initializer = initializer,
)
