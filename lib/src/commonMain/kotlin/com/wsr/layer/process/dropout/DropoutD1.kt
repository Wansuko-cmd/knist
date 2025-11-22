package com.wsr.layer.process.dropout

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.batch.times.times
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.nextFloat
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class DropoutD1 internal constructor(
    override val outputSize: Int,
    private val ratio: Float,
    private val seed: Int? = null,
) : Process.D1() {
    private val random by lazy { seed?.let { Random(it) } ?: Random }
    private val q = 1 / ratio

    override fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1> = input

    override fun train(
        input: Batch<IOType.D1>,
        context: Context,
        calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
    ): Batch<IOType.D1> {
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
