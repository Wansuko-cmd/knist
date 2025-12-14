package com.wsr.process.reshape.gad

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.toBatch
import com.wsr.batch.toList
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.operation.div.div
import com.wsr.core.reshape.transpose.transpose
import com.wsr.process.Context
import com.wsr.process.reshape.Reshape
import kotlinx.serialization.Serializable

@Serializable
internal class GlobalAverageD3ToD2(private val inputX: Int, private val inputY: Int, private val inputZ: Int) :
    Reshape.D3ToD2() {
    override val outputX: Int = inputY
    override val outputY: Int = inputZ

    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D2> = forward(input)

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D3> {
        val output = forward(input)
        val delta = calcDelta(output).toList()
        return List(input.size) {
            val delta = delta[it] / inputX.toFloat()
            IOType.d3(inputX, inputY, inputZ) { _, y, z -> delta[y, z] }
        }.toBatch()
    }

    private fun forward(input: Batch<IOType.D3>) = input.toList().map { input ->
        val input = input.transpose(axisI = 2, axisJ = 0, axisK = 1)
        IOType.d2(outputX, outputY) { x, y ->
            input[x, y].value.toFloatArray().average().toFloat()
        }
    }.toBatch()
}

fun <T> NetworkBuilder.D3<T>.globalAverageToD2() = addReshape(
    reshape = GlobalAverageD3ToD2(inputX, inputY, inputZ),
)
