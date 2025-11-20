package com.wsr.layer.process.bias

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.initializer.Fixed
import com.wsr.initializer.WeightInitializer
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.operator.plus
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class BiasD1 internal constructor(
    override val outputSize: Int,
    private val optimizer: Optimizer.D1,
    private var weight: IOType.D1,
) : Process.D1() {
    override fun expect(input: List<IOType.D1>, context: Context): List<IOType.D1> = input + weight

    override fun train(
        input: List<IOType.D1>,
        context: Context,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val output = input + weight
        val delta = calcDelta(output)
        weight = optimizer.adapt(weight = weight, dw = delta)
        return delta
    }
}

fun <T> NetworkBuilder.D1<T>.bias(optimizer: Optimizer = this.optimizer, initializer: WeightInitializer = Fixed(0f)) =
    addProcess(
        BiasD1(
            outputSize = inputSize,
            optimizer = optimizer.d1(inputSize),
            weight = initializer.d1(
                input = listOf(inputSize),
                output = listOf(inputSize),
                size = inputSize,
            ),
        ),
    )
