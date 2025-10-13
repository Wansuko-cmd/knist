package com.wsr.process.function.softmax

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.process.Process
import com.wsr.sum.sum
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
class SoftmaxD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = forward(input)

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> {
        val output = forward(input)
        val delta = calcDelta(output)
        return List(input.size) { i ->
            IOType.d2(outputX, outputY) { x, y ->
                delta[i][x, y] * output[i][x, y] * (1 - output[i][x, y])
            }
        }
    }

    private fun forward(input: List<IOType.D2>) = input.map { input ->
        val max = input.value.max()
        val exp = IOType.d2(shape = input.shape, value = input.value.map { exp(it - max) })
        val sum = exp.sum()
        IOType.d2(outputX, outputY) { x, y -> exp[x, y] / sum }
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.softmax() = addProcess(
    process = SoftmaxD2(outputX = inputX, outputY = inputY),
)
