package com.wsr.layer.reshape.reshape

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.Context
import com.wsr.layer.reshape.Reshape
import com.wsr.toBatch
import com.wsr.toList
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD2ToD1(override val outputSize: Int) : Reshape.D2ToD1() {
    constructor(inputX: Int, inputY: Int) : this(outputSize = inputX * inputY)

    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D1> = input.toList().map {
        IOType.d1(it.value)
    }.toBatch()

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D2> {
        val input = input.toList()
        val output = input.map { IOType.d1(it.value) }.toBatch()
        val delta = calcDelta(output).toList()
        return List(input.size) { i ->
            IOType.d2(shape = input[i].shape, value = delta[i].value)
        }.toBatch()
    }
}

fun <T> NetworkBuilder.D2<T>.reshapeToD1() = addReshape(
    reshape = ReshapeD2ToD1(inputX = inputX, inputY = inputY),
)
