package com.wsr.layer.reshape.gad

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.reshape.Reshape
import com.wsr.operator.div
import com.wsr.reshape.transpose
import kotlinx.serialization.Serializable

@Serializable
internal class GlobalAverageD3ToD2(private val inputX: Int, private val inputY: Int, private val inputZ: Int) :
    Reshape.D3ToD2() {
    override val outputX: Int = inputY
    override val outputY: Int = inputZ

    override fun expect(input: List<IOType.D3>): List<IOType.D2> = forward(input)

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D3> {
        val output = forward(input)
        val delta = calcDelta(output)
        return List(input.size) {
            val delta = delta[it] / inputX.toFloat()
            IOType.d3(inputX, inputY, inputZ) { _, y, z -> delta[y, z] }
        }
    }

    private fun forward(input: List<IOType.D3>) = input.map { input ->
        val input = input.transpose(axisI = 2, axisJ = 0, axisK = 1)
        IOType.d2(outputX, outputY) { x, y ->
            input[x, y].value.average().toFloat()
        }
    }
}

fun <T> NetworkBuilder.D3<T>.globalAverageToD2() = addReshape(
    reshape = GlobalAverageD3ToD2(inputX, inputY, inputZ),
)
