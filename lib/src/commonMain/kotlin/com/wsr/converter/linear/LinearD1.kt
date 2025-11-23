package com.wsr.converter.linear

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.toBatch
import com.wsr.batch.toList
import com.wsr.converter.Converter
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class LinearD1(override val outputSize: Int) : Converter.D1<IOType.D1>() {
    override fun encode(input: List<IOType.D1>): Batch<IOType.D1> = input.toBatch()
    override fun decode(input: Batch<IOType.D1>): List<IOType.D1> = input.toList()
}

fun NetworkBuilder.Companion.inputD1(inputSize: Int, optimizer: Optimizer, initializer: WeightInitializer) = inputD1(
    converter = LinearD1(inputSize),
    optimizer = optimizer,
    initializer = initializer,
)
