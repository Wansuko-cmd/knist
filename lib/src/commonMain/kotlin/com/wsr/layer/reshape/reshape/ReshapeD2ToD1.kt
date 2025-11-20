package com.wsr.layer.reshape.reshape

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.reshape.Reshape
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD2ToD1(override val outputSize: Int) : Reshape.D2ToD1() {
    constructor(inputX: Int, inputY: Int) : this(outputSize = inputX * inputY)

    override fun expect(input: List<IOType.D2>, context: Context): List<IOType.D1> = input.map { IOType.d1(it.value) }

    override fun train(input: List<IOType.D2>, context: Context, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D2> {
        val output = input.map { IOType.d1(it.value) }
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d2(shape = input[i].shape, value = delta[i].value)
        }
    }
}

fun <T> NetworkBuilder.D2<T>.reshapeToD1() = addReshape(
    reshape = ReshapeD2ToD1(inputX = inputX, inputY = inputY),
)
