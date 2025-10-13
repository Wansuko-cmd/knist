package com.wsr.reshape.gad

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.average.average
import com.wsr.operator.div
import com.wsr.reshape.Reshape
import kotlinx.serialization.Serializable

@Serializable
internal class GlobalAverageD2ToD1(private val inputX: Int, private val inputY: Int) : Reshape.D2ToD1() {
    override val outputSize: Int = inputX

    override fun expect(input: List<IOType.D2>): List<IOType.D1> = input.map { input ->
        IOType.d1(outputSize) { input[it].average() }
    }

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D2> {
        val output = input.map { input -> IOType.d1(outputSize) { input[it].average() } }
        val delta = calcDelta(output)
        return List(input.size) {
            val delta = delta[it] / inputY.toDouble()
            IOType.d2(inputX, inputY) { x, _ -> delta[x] }
        }
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.globalAverageToD1() = addReshape(
    reshape = GlobalAverageD2ToD1(inputX, inputY),
)
