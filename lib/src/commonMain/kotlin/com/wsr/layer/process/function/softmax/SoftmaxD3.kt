package com.wsr.layer.process.function.softmax

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.collection.map
import com.wsr.batch.minus.minus
import com.wsr.batch.times.times
import com.wsr.collection.sum
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
class SoftmaxD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> = forward(input)

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        val output = forward(input)
        val delta = calcDelta(output)
        return delta * output * (1f - output)
    }

    private fun forward(input: Batch<IOType.D3>) = input.map { input ->
        val max = input.value.max()
        val exp = IOType.d3(shape = input.shape, value = input.value.map { exp(it - max) })
        val sum = exp.sum()
        IOType.d3(outputX, outputY, outputZ) { x, y, z -> exp[x, y, z] / sum }
    }
}

fun <T> NetworkBuilder.D3<T>.softmax() = addProcess(
    process = SoftmaxD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)
