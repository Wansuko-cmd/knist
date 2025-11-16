package com.wsr.layer.process.dropout

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.process.Process
import com.wsr.nextFloat
import com.wsr.operator.times
import kotlin.random.Random
import kotlinx.serialization.Serializable

@Serializable
class DropoutD1 internal constructor(
    override val outputSize: Int,
    private val ratio: Float,
    private val seed: Int? = null,
) : Process.D1() {
    private val random by lazy { seed?.let { Random(it) } ?: Random }
    private val q = 1 / ratio

    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input

    override fun train(input: List<IOType.D1>, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D1> {
        val mask = IOType.d1(outputSize) { if (random.nextFloat(0f, 1f) <= ratio) q else 0f }
        val output = input * mask
        val delta = calcDelta(output)
        return delta * mask
    }
}

fun <T> NetworkBuilder.D1<T>.dropout(ratio: Float, seed: Int? = null) = addProcess(
    process =
    DropoutD1(
        outputSize = inputSize,
        ratio = ratio,
        seed = seed,
    ),
)
