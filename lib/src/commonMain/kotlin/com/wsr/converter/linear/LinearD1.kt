package com.wsr.converter.linear

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.NetworkBuilder.D1
import com.wsr.converter.Converter
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class LinearD1(override val outputSize: Int) : Converter.D1<IOType.D1>() {
    override fun encode(input: List<IOType.D1>): List<IOType.D1> = input
    override fun decode(input: List<IOType.D1>): List<IOType.D1> = input
}

fun NetworkBuilder.Companion.inputD1(inputSize: Int, optimizer: Optimizer, seed: Int? = null) = D1<IOType.D1>(
    inputSize = inputSize,
    optimizer = optimizer,
    random = seed?.let { Random(it) } ?: Random,
    input = LinearD1(inputSize),
    layers = emptyList(),
)
