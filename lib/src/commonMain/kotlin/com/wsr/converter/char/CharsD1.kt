package com.wsr.converter.char

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import com.wsr.toBatch
import com.wsr.toList
import kotlinx.serialization.Serializable

@Serializable
class CharsD1(override val outputSize: Int) : Converter.D1<String>() {
    override fun encode(input: List<String>): Batch<IOType.D1> = input.toList().map { text ->
        IOType.D1(
            value = FloatArray(outputSize) { index ->
                text.getOrNull(index)
                    ?.let { charToId[it] }
                    ?: 0f
            },
        )
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
