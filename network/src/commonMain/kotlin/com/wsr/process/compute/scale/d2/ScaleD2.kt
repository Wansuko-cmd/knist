package com.wsr.process.compute.scale.d2

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.operation.times.times
import com.wsr.core.IOType
import com.wsr.initializer.Fixed
import com.wsr.initializer.WeightInitializer
import com.wsr.optimizer.Optimizer
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import kotlinx.serialization.Serializable

@Serializable
class ScaleD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Compute.D2() {
    override fun expect(
        input: Batch<IOType.D2>,
        context: Context,
    ): Batch<IOType.D2> = input * weight

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val output = input * weight
        val delta = calcDelta(output)

        weight = optimizer.adapt(
            weight = weight,
            dw = input * delta,
        )

        return delta * weight
    }
}

fun <T> NetworkBuilder.D2<T>.scale(
    axis: Int? = null,
    optimizer: Optimizer = this.optimizer,
    initializer: WeightInitializer = Fixed(1f),
): NetworkBuilder.D2<T> {
    val process = when (axis) {
        null -> ScaleD2(
            outputX = inputX,
            outputY = inputY,
            optimizer = optimizer.d2(
                inputX,
                inputY,
            ),
            weight = initializer.d2(
                input = listOf(inputX, inputY),
                output = listOf(inputX, inputY),
                x = inputX,
                y = inputY,
            ),
        )

        0, 1 -> {
            val inputT = if (axis == 0) inputX else inputY
            ScaleAxisD2(
                outputX = inputX,
                outputY = inputY,
                axis = axis,
                optimizer = optimizer.d1(inputT),
                weight = initializer.d1(
                    input = listOf(inputT),
                    output = listOf(inputT),
                    size = inputT,
                ),
            )
        }


        else -> throw IllegalStateException(
            """
            invalid parameter.
            axis: $axis
            """.trimIndent(),
        )
    }
    return addProcess(process = process)
}
