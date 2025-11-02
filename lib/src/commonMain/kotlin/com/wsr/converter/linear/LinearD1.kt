package com.wsr.converter.linear

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class LinearD1(override val outputSize: Int) : Converter.D1<IOType.D1>() {
    override fun encode(input: List<IOType.D1>): List<IOType.D1> = input
    override fun decode(input: List<IOType.D1>): List<IOType.D1> = input
}

fun NetworkBuilder.Companion.inputD1(inputSize: Int, optimizer: Optimizer, initializer: WeightInitializer) = inputD1(
    converter = LinearD1(inputSize),
    optimizer = optimizer,
    initializer = initializer,
)
