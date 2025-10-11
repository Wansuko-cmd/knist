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
class BiasD2(
    override val outputX: Int,
    override val outputY: Int,
    private val optimizer: Optimizer.D2,
    private var weight: IOType.D2,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input + weight

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> {
        val output = input + weight
        val delta = calcDelta(output)
        weight -= optimizer.adapt(weight, delta.average())
        return delta
    }
}

fun <T : IOType> NetworkBuilder.D2<T>.bias(optimizer: Optimizer = this.optimizer) = addProcess(
    process = BiasD2(
        outputX = inputX,
        outputY = inputY,
        optimizer = optimizer.d2(inputX, inputY),
        weight = IOType.d2(inputX, inputY) { _, _ -> random.nextDouble(-1.0, 1.0) },
    ),
)
