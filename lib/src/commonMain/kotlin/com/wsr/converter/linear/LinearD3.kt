package com.wsr.converter.linear

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.converter.Converter
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class LinearD3(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Converter.D3<IOType.D3>() {
    override fun encode(input: List<IOType.D3>): List<IOType.D3> = input
    override fun decode(input: List<IOType.D3>): List<IOType.D3> = input
}

fun NetworkBuilder.Companion.inputD3(x: Int, y: Int, z: Int, optimizer: Optimizer, initializer: WeightInitializer) =
    inputD3(
        converter = LinearD3(x, y, z),
        optimizer = optimizer,
        initializer = initializer,
    )
