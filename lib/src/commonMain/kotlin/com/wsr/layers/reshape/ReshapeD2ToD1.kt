package com.wsr.layers.reshape

import com.wsr.common.IOType
import com.wsr.layers.Layer
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD2ToD1(val outputSize: Int) : Layer.Reshape() {
    constructor(x: Int, y: Int): this(outputSize = x * y)

    override fun expect(input: IOType): IOType {
        val input = input as IOType.D2
        return IOType.D1(input.value)
    }

    override fun train(
        input: IOType,
        delta: (IOType) -> IOType,
    ): IOType {
        val input = input as IOType.D2
        val output = IOType.D1(input.value)
        val delta = delta(output) as IOType.D1
        return IOType.D2(delta.value, input.shape)
    }
}
