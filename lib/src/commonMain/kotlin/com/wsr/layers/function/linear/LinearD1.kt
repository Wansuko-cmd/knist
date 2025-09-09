package com.wsr.layers.function.linear

import com.wsr.NetworkBuilder
import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
class LinearD1 internal constructor(override val outputSize: Int) : Layer.D1() {
    override fun expect(input: IOType.D1): IOType.D1 = input

    override fun train(
        input: IOType.D1,
        calcDelta: (IOType.D1) -> IOType.D1,
    ): IOType.D1 = calcDelta(input)

    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> = calcDelta(input)
}

fun <T : IOType> NetworkBuilder.D1<T>.linear() = addLayer(LinearD1(outputSize = inputSize))
