package com.wsr.layers.reshape

import com.wsr.IOType
import com.wsr.layers.Process
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD2ToD1(val outputSize: Int) : Process.Reshape() {
    constructor(x: Int, y: Int): this(outputSize = x * y)

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
