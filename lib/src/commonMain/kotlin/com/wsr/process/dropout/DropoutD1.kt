package com.wsr.process.dropout

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.operator.times
import com.wsr.process.Process
import kotlin.random.Random
import kotlinx.serialization.Serializable

@Serializable
class DropoutD1 internal constructor(
    override val outputSize: Int,
    private val ratio: Double,
    private val seed: Int? = null,
) : Process.D1() {
    private val random by lazy { seed?.let { Random(it) } ?: Random }

    override fun expect(input: List<IOType.D1>): List<IOType.D1> = ratio * input

    override fun train(input: List<IOType.D1>, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D1> {
        val mask = IOType.d1(outputSize) { if (random.nextDouble(0.0, 1.0) <= ratio) 1.0 else 0.0 }
        val output = input.map { input -> IOType.d1(outputSize) { input[it] * mask[it] } }
        val delta = calcDelta(output)
        return delta.map { delta -> IOType.d1(outputSize) { delta[it] * mask[it] } }
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.dropout(ratio: Double, seed: Int? = null) = addProcess(
    process =
    DropoutD1(
        outputSize = inputSize,
        ratio = ratio,
        seed = seed,
    ),
)
