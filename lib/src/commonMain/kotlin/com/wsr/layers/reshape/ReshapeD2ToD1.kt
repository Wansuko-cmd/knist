package com.wsr.layers.reshape

import com.wsr.common.IOType
import com.wsr.layers.Layer

class ReshapeD2ToD1(
    override val inputShape: List<Int>,
    override val outputShape: List<Int>
) : Layer.Reshape() {
    override fun expect(input: IOType): IOType {
        val input = input as IOType.D2
        return IOType.D1(input.value.flatten().toMutableList())
    }

    override fun train(
        input: IOType,
        delta: (IOType) -> IOType,
    ): IOType {
        val input = input as IOType.D2
        val output = IOType.D1(input.value.flatten().toMutableList())
        val delta = delta(output) as IOType.D1
        return IOType.D2(inputShape[0]) { axis0 ->
            IOType.D1(inputShape[1]) { axis1 ->
                delta[axis0 * axis1 + axis1]
            }
        }
    }
}
