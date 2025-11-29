package com.wsr.layer.reshape.reshape

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.reshape.flatten.flatten
import com.wsr.batch.reshape.reshape.reshapeToD2
import com.wsr.core.IOType
import com.wsr.layer.Context
import com.wsr.layer.reshape.Reshape
import kotlinx.serialization.Serializable

@Serializable
internal class ReshapeD2ToD1(override val outputSize: Int) : Reshape.D2ToD1() {
    constructor(inputX: Int, inputY: Int) : this(outputSize = inputX * inputY)

    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D1> = input.flatten()

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D2> {
        val output = input.flatten()
        val delta = calcDelta(output)
        return delta.reshapeToD2(input.shape)
    }
}

fun <T> NetworkBuilder.D2<T>.reshapeToD1() = addReshape(
    reshape = ReshapeD2ToD1(inputX = inputX, inputY = inputY),
)
