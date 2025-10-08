package com.wsr.output.mean

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.operator.minus
import com.wsr.output.Output
import kotlinx.serialization.Serializable

@Serializable
internal class MeanSquareD1 internal constructor(val outputSize: Int) : Output.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input

    override fun train(input: List<IOType.D1>, label: List<IOType.D1>): List<IOType.D1> = List(input.size) { i ->
        input[i] -
            label[i]
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.meanSquare() = addOutput(MeanSquareD1(inputSize))
