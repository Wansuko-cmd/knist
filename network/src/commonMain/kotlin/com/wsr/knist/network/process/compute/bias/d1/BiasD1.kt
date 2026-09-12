package com.wsr.knist.network.process.compute.bias.d1

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.initializer.Fixed
import com.wsr.knist.network.initializer.WeightInitializer
import com.wsr.knist.network.optimizer.Optimizer
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class BiasD1 internal constructor(override val inputI: Int, private var optimizer: Optimizer.D1, private var weight: IOType.D1.Global, override val id: String = Uuid.random().toString()) :
    Compute.D1() {
    override val outputI: Int get() = inputI
    override fun IOScope.expect(input: Batch<IOType.D1>, env: GraphEnv): Batch<IOType.D1> = input + weight

    override fun IOScope.train(input: Batch<IOType.D1>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D1> {
        val output = input + weight
        val delta = calcDelta(output)
        weight = optimizer.adapt(weight = weight, dw = delta).toGlobal()
        return delta
    }

    override fun update(optimizer: Optimizer) {
        this.optimizer = optimizer.d1(i = inputI)
    }
}

fun GraphBuilder.Node.D1.bias(optimizer: Optimizer = this.optimizer, initializer: WeightInitializer = Fixed(0f), id: String = Uuid.random().toString()) = addCompute(
    BiasD1(
        inputI = inputI,
        optimizer = optimizer.d1(inputI),
        weight = initializer.d1(
            input = listOf(inputI),
            output = listOf(inputI),
            size = inputI,
        ),
        id = id,
    ),
)
