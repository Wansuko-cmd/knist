package com.wsr.process.bias

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.average.average
import com.wsr.operator.minus
import com.wsr.operator.plus
import com.wsr.optimizer.Optimizer
import com.wsr.process.Process
import kotlinx.serialization.Serializable

@Serializable
class BiasD1 internal constructor(
    override val outputSize: Int,
    private val optimizer: Optimizer.D1,
    private var weight: IOType.D1,
) : Process.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input + weight

    override fun train(input: List<IOType.D1>, calcDelta: (List<IOType.D1>) -> List<IOType.D1>): List<IOType.D1> {
        val output = input + weight
        val delta = calcDelta(output)
        weight = optimizer.adapt(weight, delta.average())
        return delta
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.bias(optimizer: Optimizer = this.optimizer) = addProcess(
    BiasD1(
        outputSize = inputSize,
        optimizer = optimizer.d1(inputSize),
        weight = IOType.d1(inputSize) { random.nextDouble(-1.0, 1.0) },
    ),
)
