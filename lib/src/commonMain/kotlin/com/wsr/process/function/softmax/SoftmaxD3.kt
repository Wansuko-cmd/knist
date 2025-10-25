package com.wsr.process.function.softmax

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.sum
import com.wsr.process.Process
import kotlin.math.exp
import kotlinx.serialization.Serializable

@Serializable
class SoftmaxD3 internal constructor(override val outputX: Int, override val outputY: Int, override val outputZ: Int) :
    Process.D3() {
    override fun expect(input: List<IOType.D3>): List<IOType.D3> = forward(input)

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D3>) -> List<IOType.D3>): List<IOType.D3> {
        val output = forward(input)
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d3(outputX, outputY, outputZ) { x, y, z ->
                delta[i][x, y, z] * output[i][x, y, z] * (1 - output[i][x, y, z])
            }
        }
    }

    private fun forward(input: List<IOType.D3>) = input.map { input ->
        val max = input.value.max()
        val exp = IOType.d3(shape = input.shape, value = input.value.map { exp(it - max) })
        val sum = exp.sum()
        IOType.d3(outputX, outputY, outputZ) { x, y, z -> exp[x, y, z] / sum }
    }
}

fun <T> NetworkBuilder.D3<T>.softmax() = addProcess(
    process = SoftmaxD3(outputX = inputX, outputY = inputY, outputZ = inputZ),
)
