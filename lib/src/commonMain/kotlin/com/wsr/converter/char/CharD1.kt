package com.wsr.converter.char

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class CharD1() : Converter.D1<Char>() {
    override val outputSize = chars.size
    override fun encode(input: List<Char>): List<IOType.D1> = input.map { char ->
        val id = charToId[char] ?: 0
        IOType.d1(outputSize).also { it[id] = 1.0 }
    }

    override fun decode(input: List<IOType.D1>): List<Char> = input.map { input ->
        val index = input.maxIndex() ?: 0
        chars[index]
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
