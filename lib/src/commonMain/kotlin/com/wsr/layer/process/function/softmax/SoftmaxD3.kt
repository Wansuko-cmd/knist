package com.wsr.layer.process.function.softmax

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.sum
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.toBatch
import com.wsr.toList
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
class SoftmaxD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> = forward(input)

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val output = forward(input).toList()
        val delta = calcDelta(output.toBatch()).toList()
        return List(input.size) { i ->
            IOType.d3(outputX, outputY, outputZ) { x, y, z ->
                delta[i][x, y, z] * output[i][x, y, z] * (1 - output[i][x, y, z])
            }
        }.toBatch()
    }

    private fun forward(input: Batch<IOType.D3>) = input.toList().map { input ->
        val max = input.value.max()
        val exp = IOType.d3(shape = input.shape, value = input.value.map { exp(it - max) })
        val sum = exp.sum()
        IOType.d3(outputX, outputY, outputZ) { x, y, z -> exp[x, y, z] / sum }
    }.toBatch()
}

fun <T> NetworkBuilder.D3<T>.softmax() = addProcess(
    process = SoftmaxD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)
