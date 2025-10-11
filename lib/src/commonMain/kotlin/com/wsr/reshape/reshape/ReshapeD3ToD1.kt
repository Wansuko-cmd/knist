package com.wsr.reshape.reshape

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.NetworkBuilder.D1
import com.wsr.reshape.Reshape
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD3ToD1(override val outputSize: Int) : Reshape.D3ToD1() {
    constructor(inputX: Int, inputY: Int, inputZ: Int) : this(outputSize = inputX * inputY * inputZ)

    override fun expect(input: List<IOType.D3>): List<IOType.D1> = input.map {
        IOType.d1(value = it.value)
    }

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D3> {
        val output = input.map { IOType.d1(value = it.value) }
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d3(shape = input[i].shape, value = delta[i].value)
        }
    }
}

fun <T : IOType> NetworkBuilder.D3<T>.reshapeToD1(): D1<T> = addReshape(
    reshape = ReshapeD3ToD1(inputX = inputX, inputY = inputY, inputZ = inputZ),
)
