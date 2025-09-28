package com.wsr.layers.function.linear

import com.wsr.NetworkBuilder
import com.wsr.IOType
import com.wsr.layers.Process
import kotlinx.serialization.Serializable

@Serializable
class LinearD1 internal constructor(override val outputSize: Int) : Process.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> = calcDelta(input)
}

fun <T : IOType> NetworkBuilder.D1<T>.linear() = addLayer(LinearD1(outputSize = inputSize))
