package com.wsr.process.bias

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.collection.batchAverage
import com.wsr.operator.plus
import com.wsr.optimizer.Optimizer
import com.wsr.process.Process
import kotlinx.serialization.Serializable

@Serializable
class BiasD3(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val optimizer: Optimizer.D3,
    private var weight: IOType.D3,
) : Process.D3() {
    override fun expect(input: List<IOType.D3>): List<IOType.D3> = input + weight

    override fun train(input: List<IOType.D3>, calcDelta: (List<IOType.D3>) -> List<IOType.D3>): List<IOType.D3> {
        val output = input + weight
        val delta = calcDelta(output)
        weight = optimizer.adapt(weight = weight, dw = delta.batchAverage())
        return delta
    }
}

fun <T> NetworkBuilder.D3<T>.bias(optimizer: Optimizer = this.optimizer) = addProcess(
    process = BiasD3(
        outputX = inputX,
        outputY = inputY,
        outputZ = inputZ,
        optimizer = optimizer.d3(inputX, inputY, inputZ),
        weight = IOType.d3(inputX, inputY, inputZ) { _, _, _ -> random.nextDouble(-1.0, 1.0) },
    ),
)
