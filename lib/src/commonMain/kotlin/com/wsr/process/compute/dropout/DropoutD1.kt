package com.wsr.process.compute.dropout

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.core.d1
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import com.wsr.nextFloat
import kotlin.random.Random
import kotlinx.serialization.Serializable

@Serializable
class DropoutD1 internal constructor(
    override val outputSize: Int,
    private val ratio: Float,
    private val seed: Int? = null,
) : Compute.D1() {
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
