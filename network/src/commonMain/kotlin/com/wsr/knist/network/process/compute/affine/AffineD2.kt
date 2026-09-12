package com.wsr.knist.network.process.compute.affine

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.initializer.WeightInitializer
import com.wsr.knist.network.optimizer.Optimizer
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class AffineD2 internal constructor(
    private val channel: Int,
    override val inputJ: Int,
    override val outputJ: Int,
    private var optimizer: Optimizer.D2,
    private var weight: IOType.D2.Global,
    override val id: String = Uuid.random().toString(),
) : Compute.D2() {
    override val inputI: Int get() = channel

    override val outputI = channel

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> = input.matMul(weight)

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val output = input.matMul(weight)
        val delta = calcDelta(output)

        val dx = delta.matMul(weight, transB = true)
        val dw = input.matMul(delta, transA = true)

        weight = optimizer.adapt(weight = weight, dw = dw).toGlobal()
        return dx
    }

    override fun update(optimizer: Optimizer) {
        this.optimizer = optimizer.d2(inputJ, outputJ)
    }
}

fun GraphBuilder.Node.D2.affine(neuron: Int, optimizer: Optimizer = this.optimizer, initializer: WeightInitializer = this.initializer, id: String = Uuid.random().toString()) = addCompute(
    compute =
        AffineD2(
            channel = inputI,
            inputJ = inputJ,
            outputJ = neuron,
            optimizer = optimizer.d2(inputJ, neuron),
            weight = initializer.d2(
                input = listOf(inputJ),
                output = listOf(neuron),
                i = inputJ,
                j = neuron,
            ),
            id = id,
        ),
)
