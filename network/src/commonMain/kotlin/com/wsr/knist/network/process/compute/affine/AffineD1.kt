package com.wsr.knist.network.process.compute.affine

import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.linalg.matMul
import com.wsr.knist.batch.shape.toBatch
import com.wsr.knist.batch.shape.toD2
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
class AffineD1 internal constructor(
    override val inputI: Int,
    override val outputI: Int,
    private var optimizer: Optimizer.D2,
    private var weight: IOType.D2.Global,
    override val id: String = Uuid.random().toString(),
) : Compute.D1() {
    override fun IOScope.expect(input: Batch<IOType.D1>, env: GraphEnv): Batch<IOType.D1> = input.matMul(weight)

    override fun IOScope.train(input: Batch<IOType.D1>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D1> {
        val output = input.matMul(weight)
        val delta = calcDelta(output)
        val dx = delta.matMul(weight, trans = true)
        val dw = input.toD2().matMul(delta.toD2(), transA = true) / input.size.toFloat()
        weight = optimizer.adapt(weight = weight, dw = dw).toGlobal()
        return dx
    }

    override fun update(optimizer: Optimizer) {
        this.optimizer = optimizer.d2(i = inputI, j = outputI)
    }
}

fun GraphBuilder.Node.D1.affine(neuron: Int, optimizer: Optimizer = this.optimizer, initializer: WeightInitializer = this.initializer, id: String = Uuid.random().toString()) = addCompute(
    compute =
        AffineD1(
            inputI = inputI,
            outputI = neuron,
            optimizer = optimizer.d2(inputI, neuron),
            weight = initializer.d2(
                input = listOf(inputI),
                output = listOf(neuron),
                i = inputI,
                j = neuron,
            ),
            id = id,
        ),
)
