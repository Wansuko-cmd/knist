package com.wsr.output.sigmoid

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.operator.minus
import com.wsr.output.Output
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
internal class SigmoidWithLossD1 internal constructor(val outputSize: Int) : Output.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input

    override fun train(input: List<IOType.D1>, label: List<IOType.D1>): List<IOType.D1> {
        val output = input.map { (value) ->
            IOType.d1(outputSize) { 1 / (1 + exp(-value[it])) }
        }
        return List(input.size) { i -> output[i] - label[i] }
    }
}

fun <T: IOType> NetworkBuilder.D1<T>.sigmoidWithLoss() = addOutput(SigmoidWithLossD1(inputSize))
