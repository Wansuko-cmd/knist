package com.wsr.process.affine

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.d1.toD2
import com.wsr.d2.dot
import com.wsr.d2.toD3
import com.wsr.d2.transpose
import com.wsr.d3.transpose
import com.wsr.operation.minus
import com.wsr.operation.times
import com.wsr.process.Process
import kotlinx.serialization.Serializable

@Serializable
class AffineD2 internal constructor(
    private val channel: Int,
    private val outputSize: Int,
    private val rate: Double,
    private var weight: IOType.D3,
) : Process.D2() {
    override val outputX = channel
    override val outputY = outputSize

    override fun expect(input: List<IOType.D2>): List<IOType.D2> = forward(input)

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val output = forward(input)
        val delta = calcDelta(output)
        val dx = delta.map { delta -> (0 until channel).map { weight[it].dot(delta[it]) }.toD2() }
        val dwi = input.toD3().transpose(1, 2, 0)
        val dwd = delta.toD3().transpose(1, 0, 2)
        val dw = (0 until channel).map { dwi[it].dot(dwd[it]) }.toD3()
        weight -= rate / input.size * dw
        return dx
    }

    private fun forward(input: List<IOType.D2>): List<IOType.D2> {
        val weight = (0 until channel).map { weight[it].transpose() }
        return input.map { input ->
            (0 until channel)
                .map { weight[it].dot(input[it]) }
                .toD2()
        }
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.affine(neuron: Int) =
    addProcess(
        process = AffineD2(
            channel = inputX,
            outputSize = neuron,
            rate = rate,
            weight = IOType.d3(inputX, inputY, neuron) { _, _, _ ->
                random.nextDouble(-1.0, 1.0)
            },
        ),
    )
