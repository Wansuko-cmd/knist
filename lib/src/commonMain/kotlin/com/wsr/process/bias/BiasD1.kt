package com.wsr.process.bias

import com.wsr.NetworkBuilder
import com.wsr.IOType
import com.wsr.d1.average
import com.wsr.d1.minus
import com.wsr.d1.plus
import com.wsr.d1.times
import com.wsr.process.Process
import kotlinx.serialization.Serializable

@Serializable
class BiasD1 internal constructor(
    override val outputSize: Int,
    private val rate: Double,
    private var weight: IOType.D1,
) : Process.D1() {
    override fun expect(input: List<IOType.D1>): List<IOType.D1> = input + weight

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
    ): List<IOType.D1> {
        val output = input + weight
        val delta = calcDelta(output)
        weight -= rate * delta.average()
        return delta
    }
}

fun <T : IOType> NetworkBuilder.D1<T>.bias() = addProcess(
    BiasD1(
        outputSize = inputSize,
        rate = rate,
        weight = IOType.d1(inputSize) { random.nextDouble(-1.0, 1.0) },
    ),
)