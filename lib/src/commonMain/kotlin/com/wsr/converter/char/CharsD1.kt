package com.wsr.converter.char

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class CharsD1(override val outputSize: Int) : Converter.D1<String>() {
    override fun encode(input: List<String>): List<IOType.D1> = input.map { text ->
        IOType.D1(
            value = DoubleArray(outputSize) { index ->
                text.getOrNull(index)
                    ?.let { charToId[it] }
                    ?: 0.0
            },
        )
    }

    override fun decode(input: List<IOType.D1>): List<String> = input.map { input ->
        input.value
            .map { chars.getOrNull(it.toInt()) }
            .filterNotNull()
            .joinToString("")
    }

    companion object Companion {
        private val chars = " abcdefghijklmnopqrstuvwxyz.,!?".toList()
        private val charToId = chars.mapIndexed { index, char -> char to index.toDouble() }.toMap()
        val vocabSize = chars.size
    }
}

fun NetworkBuilder.Companion.charsD1(maxLength: Int, optimizer: Optimizer, initializer: WeightInitializer) = inputD1(
    converter = CharsD1(maxLength),
    optimizer = optimizer,
    initializer = initializer,
)
