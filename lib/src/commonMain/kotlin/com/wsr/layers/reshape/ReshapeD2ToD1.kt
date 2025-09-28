package com.wsr.layers.reshape

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.reshape.Reshape
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD2ToD1(override val outputSize: Int) : Reshape.D2ToD1() {
    constructor(x: Int, y: Int): this(outputSize = x * y)

    override fun expect(input: List<IOType.D2>): List<IOType.D1> = input.map { IOType.d1(it.value) }

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D2> {
        val output = input.map { IOType.d1(it.value) }
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d2(shape = input[i].shape, value = delta[i].value)
        }
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.reshapeToD1() = addReshape(ReshapeD2ToD1(inputX, inputY))
