package com.wsr.layer.reshape.gad

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.collecction.average.average
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.d3
import com.wsr.core.get
import com.wsr.core.operation.div.div
import com.wsr.layer.Context
import com.wsr.layer.reshape.Reshape
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
