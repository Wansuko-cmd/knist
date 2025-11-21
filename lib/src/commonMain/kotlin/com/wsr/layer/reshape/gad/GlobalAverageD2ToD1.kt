package com.wsr.layer.reshape.gad

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.average
import com.wsr.layer.Context
import com.wsr.layer.reshape.Reshape
import com.wsr.operator.div
import com.wsr.toBatch
import com.wsr.toList
import kotlinx.serialization.Serializable

@Serializable
internal class GlobalAverageD2ToD1(private val inputX: Int, private val inputY: Int) : Reshape.D2ToD1() {
    override val outputSize: Int = inputX

    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D1> = input.toList().map { input ->
        IOType.d1(outputSize) { input[it].average() }
    }.toBatch()

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D2> {
        val output = input.toList().map { input -> IOType.d1(outputSize) { input[it].average() } }
        val delta = calcDelta(output.toBatch()).toList()
        return List(input.size) {
            val delta = delta[it] / inputY.toFloat()
            IOType.d2(inputX, inputY) { x, _ -> delta[x] }
        }.toBatch()
    }
}

fun <T> NetworkBuilder.D2<T>.globalAverageToD1() = addReshape(
    reshape = GlobalAverageD2ToD1(inputX, inputY),
)
