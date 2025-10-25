package com.wsr.converter.linear

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.NetworkBuilder.D2
import com.wsr.converter.Converter
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class LinearD2(override val outputX: Int, override val outputY: Int) : Converter.D2<IOType.D2>() {
    override fun encode(input: List<IOType.D2>): List<IOType.D2> = input
    override fun decode(input: List<IOType.D2>): List<IOType.D2> = input
}

fun NetworkBuilder.Companion.inputD2(x: Int, y: Int, optimizer: Optimizer, seed: Int? = null) = D2<IOType.D2>(
    inputX = x,
    inputY = y,
    optimizer = optimizer,
    random = seed?.let { Random(it) } ?: Random,
    input = LinearD2(x, y),
    layers = emptyList(),
)
