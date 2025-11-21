package com.wsr.converter.linear

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
class LinearD3(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Converter.D3<IOType.D3>() {
    override fun encode(input: List<IOType.D3>): Batch<IOType.D3> = input.toBatch()
    override fun decode(input: Batch<IOType.D3>): List<IOType.D3> = input.toList()
}

fun NetworkBuilder.Companion.inputD3(x: Int, y: Int, z: Int, optimizer: Optimizer, initializer: WeightInitializer) =
    inputD3(
        converter = LinearD3(x, y, z),
        optimizer = optimizer,
        initializer = initializer,
    )
