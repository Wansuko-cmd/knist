package com.wsr.converter.linear

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.NetworkBuilder.D3
import com.wsr.converter.Converter
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class LinearD3(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Converter.D3<IOType.D3>() {
    override fun encode(input: List<IOType.D3>): List<IOType.D3> = input
    override fun decode(input: List<IOType.D3>): List<IOType.D3> = input
}

fun NetworkBuilder.Companion.inputD3(
    x: Int,
    y: Int,
    z: Int,
    optimizer: Optimizer,
    seed: Int? = null
) = D3<IOType.D3>(
    inputX = x,
    inputY = y,
    inputZ = z,
    optimizer = optimizer,
    random = seed?.let { Random(it) } ?: Random,
    input = LinearD3(x, y, z),
    layers = emptyList(),
)
