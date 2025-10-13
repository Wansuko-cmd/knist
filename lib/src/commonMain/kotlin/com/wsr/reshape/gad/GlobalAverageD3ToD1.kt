package com.wsr.reshape.gad

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.operator.div
import com.wsr.reshape.Reshape
import kotlinx.serialization.Serializable

@Serializable
internal class GlobalAverageD3ToD1(
    private val inputX: Int,
    private val inputY: Int,
    private val inputZ: Int,
) : Reshape.D3ToD1() {
    override val outputSize: Int = inputX

    override fun expect(input: List<IOType.D3>): List<IOType.D1> = input.map { input ->
        IOType.d1(outputSize) { input[it].value.average() }
    }

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D3> {
        val output = input.map { input -> IOType.d1(outputSize) { input[it].value.average() } }
        val delta = calcDelta(output)
        return List(input.size) {
            val delta = delta[it] / (inputY * inputZ).toDouble()
            IOType.d3(inputX, inputY, inputZ) { x, _, _ -> delta[x] }
        }
    }
}

fun <T : IOType> NetworkBuilder.D3<T>.globalAverageToD1() = addReshape(
    reshape = GlobalAverageD3ToD1(inputX, inputY, inputZ),
)
