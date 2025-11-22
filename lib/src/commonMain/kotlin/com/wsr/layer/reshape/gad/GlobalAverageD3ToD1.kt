package com.wsr.layer.reshape.gad

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.average.average
import com.wsr.d3
import com.wsr.get
import com.wsr.layer.Context
import com.wsr.layer.reshape.Reshape
import com.wsr.operator.div
import com.wsr.set
import kotlinx.serialization.Serializable

@Serializable
internal class GlobalAverageD3ToD1(private val inputX: Int, private val inputY: Int, private val inputZ: Int) :
    Reshape.D3ToD1() {
    override val outputSize: Int = inputX

    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D1> =
        input.average(axis = 2).average(axis = 1)

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D3> {
        val output = input.average(axis = 2).average(axis = 1)
        val delta = calcDelta(output)
        return Batch(input.size) {
            val delta = delta[it] / (inputY * inputZ).toFloat()
            IOType.d3(inputX, inputY, inputZ) { x, _, _ -> delta[x] }
        }
    }
}

fun <T> NetworkBuilder.D3<T>.globalAverageToD1() = addReshape(
    reshape = GlobalAverageD3ToD1(inputX, inputY, inputZ),
)
