package com.wsr.layers.reshape

import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD2ToD1(val outputSize: Int) : Layer.Reshape() {
    constructor(x: Int, y: Int): this(outputSize = x * y)

    override fun expect(input: IOType): IOType {
        val input = input as IOType.D2
        return IOType.d1(input.value)
    }

    override fun train(
        input: IOType,
        calcDelta: (IOType) -> IOType,
    ): IOType {
        val input = input as IOType.D2
        val output = IOType.d1(input.value)
        val delta = calcDelta(output) as IOType.D1
        return IOType.d2(shape = input.shape, value = delta.value)
    }

    override fun expect(input: List<IOType>): List<IOType> = input.map { IOType.d1(it.value) }

    override fun train(
        input: List<IOType>,
        calcDelta: (List<IOType>) -> List<IOType>,
    ): List<IOType> {
        val output = input.map { IOType.d1(it.value) }
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d2(shape = input[i].shape, value = delta[i].value)
        }
    }
}
