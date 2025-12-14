package com.wsr.converter.linear

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.toBatch
import com.wsr.batch.toList
import com.wsr.converter.Converter
import com.wsr.core.IOType
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class LinearD2(override val outputX: Int, override val outputY: Int) : Converter.D2<IOType.D2>() {
    override fun encode(input: List<IOType.D2>): Batch<IOType.D2> = input.toBatch()
    override fun decode(input: Batch<IOType.D2>): List<IOType.D2> = input.toList()
}

fun NetworkBuilder.Companion.inputD2(x: Int, y: Int, optimizer: Optimizer, initializer: WeightInitializer) = inputD2(
    converter = LinearD2(x, y),
    optimizer = optimizer,
    initializer = initializer,
)
