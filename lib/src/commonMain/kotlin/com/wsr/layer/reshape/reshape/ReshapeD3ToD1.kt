package com.wsr.layer.reshape.reshape

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.NetworkBuilder.D1
import com.wsr.layer.Context
import com.wsr.layer.reshape.Reshape
import com.wsr.toBatch
import com.wsr.toList
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD3ToD1(override val outputSize: Int) : Reshape.D3ToD1() {
    constructor(inputX: Int, inputY: Int, inputZ: Int) : this(outputSize = inputX * inputY * inputZ)

    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D1> = input.toList().map {
        IOType.d1(value = it.value)
    }.toBatch()

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D3> {
        val input = input.toList()
        val output = input.map { IOType.d1(value = it.value) }
        val delta = calcDelta(output.toBatch()).toList()
        return List(input.size) { i ->
            IOType.d3(shape = input[i].shape, value = delta[i].value)
        }.toBatch()
    }
}

fun <T> NetworkBuilder.D3<T>.reshapeToD1(): D1<T> = addReshape(
    reshape = ReshapeD3ToD1(inputX = inputX, inputY = inputY, inputZ = inputZ),
)
