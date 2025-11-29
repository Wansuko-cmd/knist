package com.wsr.layer.reshape.reshape

import com.wsr.NetworkBuilder
import com.wsr.NetworkBuilder.D1
import com.wsr.batch.Batch
import com.wsr.batch.reshape.flatten.flatten
import com.wsr.batch.reshape.reshape.reshapeToD3
import com.wsr.core.IOType
import com.wsr.layer.Context
import com.wsr.layer.reshape.Reshape
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD3ToD1(override val outputSize: Int) : Reshape.D3ToD1() {
    constructor(inputX: Int, inputY: Int, inputZ: Int) : this(outputSize = inputX * inputY * inputZ)

    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D1> = input.flatten()

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D3> {
        val output = input.flatten()
        val delta = calcDelta(output)
        return delta.reshapeToD3(input.shape)
    }
}

fun <T> NetworkBuilder.D3<T>.reshapeToD1(): D1<T> = addReshape(
    reshape = ReshapeD3ToD1(inputX = inputX, inputY = inputY, inputZ = inputZ),
)
